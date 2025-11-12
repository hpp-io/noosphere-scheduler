package io.hpp.noosphere.scheduler.service.blockchain;

import io.hpp.noosphere.scheduler.service.blockchain.dto.*;
import io.hpp.noosphere.scheduler.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BlockChainService {

    private static final Logger log = LoggerFactory.getLogger(BlockChainService.class);
    private static final String BLOCKED_TX = "0xblocked";

    private record ShouldProcessResult(boolean should, byte[] commitment) {}

    private final Web3j web3j;
    private final CoordinatorService coordinator;
    private final WalletService wallet;

    // State management using thread-safe collections
    private final Map<Long, SubscriptionDTO> subscriptions = new ConcurrentHashMap<>();
    private final Map<DelegatedSubscriptionId, DelegatedSubscriptionData> delegateSubscriptions = new ConcurrentHashMap<>();
    private final Map<SubscriptionRunKey, String> pendingTxs = new ConcurrentHashMap<>();
    private final Map<SubscriptionRunKey, AtomicInteger> txAttempts = new ConcurrentHashMap<>();

    public BlockChainService(Web3j web3j, CoordinatorService coordinator, WalletService wallet) {
        this.web3j = web3j;
        this.coordinator = coordinator;
        this.wallet = wallet;
    }

    /**
     * Tracks incoming on-chain messages.
     */
    @Async
    public CompletableFuture<Void> processIncomingRequest(BaseRequestDTO request) {
        if (request instanceof OnchainRequestDTO onchainRequestDTO) {
            ProcessOnchainRequest(onchainRequestDTO);
            return CompletableFuture.completedFuture(null);
        } else if (request instanceof DelegatedRequestDTO delegatedRequestDTO) {
            return ProcessDelegatedRequest(delegatedRequestDTO);
        } else {
            log.error("Unknown request type to track: {}", request);
            return CompletableFuture.failedFuture(new IllegalArgumentException("Unknown request type"));
        }
    }

    private void ProcessOnchainRequest(OnchainRequestDTO requestDTO) {
        subscriptions.put(requestDTO.getSubscription().getId(), requestDTO.getSubscription());
        log.info("Tracked new subscription! id={}, total={}", requestDTO.getSubscription().getId(), subscriptions.size());
    }

    private CompletableFuture<Void> ProcessDelegatedRequest(DelegatedRequestDTO requestDTO) {
        // 1. Collect message inputs
        SubscriptionDTO subscription = requestDTO.getSubscription();
        SignatureParamsDTO signature = requestDTO.getSignature();

        // Start the async chain by fetching the latest block number asynchronously
        return CompletableFuture.supplyAsync(() -> {
            try {
                return web3j.ethBlockNumber().send().getBlockNumber().longValue();
            } catch (IOException e) {
                log.error("Failed to get latest block number", e);
                throw new RuntimeException(e);
            }
        }).thenCompose(headBlock ->
            // 2. Check if delegated subscription already exists on-chain
            coordinator
                .getExistingDelegateSubscription(subscription, signature, headBlock)
                .thenCompose(existingSub -> {
                    if (existingSub.exists()) {
                        // 3. If so, evict relevant run from pending
                        log.info(
                            "Delegated subscription exists on-chain with ID: {}, tracked locally: {}",
                            existingSub.subscriptionId(),
                            subscriptions.containsKey(existingSub.subscriptionId())
                        );

                        // Evict current delegate runs from pending
                        // The interval might not be known at this point, so we use a placeholder or a convention.
                        // Using 0 assumes we want to clear any pending run for this delegated ID.
                        SubscriptionRunKey key = new SubscriptionRunKey(
                            new DelegatedSubscriptionId(subscription.getClient(), signature.nonce()),
                            0
                        );
                        synchronized (this) {
                            if (pendingTxs.remove(key) != null) {
                                log.info("Evicted past pending subscription tx for run: {}", key);
                            }
                            if (txAttempts.remove(key) != null) {
                                log.info("Evicted past pending subscription attempts for run: {}", key);
                            }
                        }
                        return CompletableFuture.completedFuture(null); // End of this path
                    } else {
                        // 4. If not, verify that recovered signer == delegated signer and track it
                        return verifyAndTrackDelegation(subscription, signature, headBlock, requestDTO.getData());
                    }
                })
        );
    }

    private CompletableFuture<Void> verifyAndTrackDelegation(
        SubscriptionDTO subscription,
        SignatureParamsDTO signature,
        long headBlock,
        Map<String, Object> data
    ) {
        // Run both async calls in parallel
        CompletableFuture<String> recoveredSignerFuture = coordinator.recoverDelegateeSigner(subscription, signature);
        CompletableFuture<String> delegatedSignerFuture = coordinator.getDelegatedSigner(subscription, headBlock);

        return recoveredSignerFuture
            .thenCombine(delegatedSignerFuture, (recoveredSigner, delegatedSigner) -> {
                if (!recoveredSigner.equalsIgnoreCase(delegatedSigner)) {
                    // If signers don't match, throw an exception to fail the future
                    throw new IllegalStateException(
                        "Subscription signer mismatch. Recovered: " + recoveredSigner + ", Delegated: " + delegatedSigner
                    );
                }
                // If they match, return the verified signer for the next step
                return recoveredSigner;
            })
            .thenAccept(verifiedSigner -> {
                // 5. If verified, adds subscription to _delegate_subscriptions
                log.debug("Successfully verified delegated signer: {}", verifiedSigner);
                DelegatedSubscriptionId subId = new DelegatedSubscriptionId(subscription.getClient(), signature.nonce());
                DelegatedSubscriptionData delegatedSubscriptionData = new DelegatedSubscriptionData(subscription, signature, data);
                delegateSubscriptions.put(subId, delegatedSubscriptionData);
                log.info("Tracked new delegate subscription: {}", subId);
            });
    }

    /**
     * Core processing loop, runs every 100ms.
     */
    @Scheduled(fixedDelay = 100)
    public void processActiveSubscriptions() {
        pruneFailedTxs();

        // Process regular subscriptions
        subscriptions.forEach((subId, subscription) -> {
            shouldProcess(new OnchainSubscriptionId(subId), subscription, false).thenAccept(result -> {
                if (result.should()) {
                    processSubscription(new OnchainSubscriptionId(subId), subscription, false, null, result.commitment());
                }
            });
        });

        // Process delegated subscriptions
        delegateSubscriptions.forEach((delegateSubId, params) -> {
            shouldProcess(delegateSubId, params.subscription(), true).thenAccept(result -> {
                if (result.should()) {
                    processSubscription(delegateSubId, params.subscription(), true, params, null);
                }
            });
        });
    }

    private CompletableFuture<ShouldProcessResult> shouldProcess(
        SubscriptionIdentifier subId,
        SubscriptionDTO subscription,
        boolean isDelegated
    ) {
        if (!subscription.isActive()) {
            return CompletableFuture.completedFuture(new ShouldProcessResult(false, null));
        }

        long interval = subscription.getInterval();
        SubscriptionRunKey runKey = new SubscriptionRunKey(subId, interval);

        if (pendingTxs.containsKey(runKey)) {
            return CompletableFuture.completedFuture(new ShouldProcessResult(false, null)); // Already processing
        }

        if (txAttempts.getOrDefault(runKey, new AtomicInteger(0)).get() >= 3) {
            log.warn("Subscription {} has exceeded max retries for interval {}.", subId, interval);
            return CompletableFuture.completedFuture(new ShouldProcessResult(false, null));
        }

        if (!isDelegated) {
            // For non-delegated, check if already responded
            return coordinator
                .getNodeHasDeliveredResponse(subscription.getId(), interval, wallet.getAddress(), null)
                .thenCompose(hasResponded -> {
                    if (hasResponded) {
                        subscription.setNodeReplied(interval);
                        return CompletableFuture.completedFuture(new ShouldProcessResult(false, null)); // Already responded, do not process
                    }
                    // If not responded, check if a valid commitment exists for this interval
                    return coordinator
                        .hasRequestCommitments(BigInteger.valueOf(subscription.getId()), BigInteger.valueOf(interval))
                        .thenCompose(hasCommitment -> {
                            if (hasCommitment) {
                                // If commitment exists, fetch it to pass it to the processing step.
                                return coordinator
                                    .getCommitment(subscription.getId(), interval)
                                    .thenApply(commitment -> new ShouldProcessResult(true, coordinator.encodeCommitment(commitment)));
                            } else {
                                // If no commitment, no need to process.
                                return CompletableFuture.completedFuture(new ShouldProcessResult(false, null));
                            }
                        });
                });
        }

        return CompletableFuture.completedFuture(new ShouldProcessResult(true, null));
    }

    @Async
    public void pruneFailedTxs() {
        pendingTxs.forEach((runKey, txHash) -> {
            if (!BLOCKED_TX.equals(txHash)) {
                coordinator
                    .getTxSuccess(txHash)
                    .thenAccept(txReceipt -> {
                        if (txReceipt != null && !txReceipt.success()) {
                            synchronized (this) {
                                int attempts = txAttempts.computeIfAbsent(runKey, k -> new AtomicInteger(0)).incrementAndGet();
                                if (attempts < 3) {
                                    pendingTxs.remove(runKey);
                                    log.info("Evicted failed tx for {}, retries: {}", runKey, attempts);
                                } else {
                                    log.error("Max retries reached for {}. It will be blocked.", runKey);
                                }
                            }
                        }
                    });
            }
        });
    }

    @Async
    public void processSubscription(
        SubscriptionIdentifier id,
        SubscriptionDTO subscription,
        boolean delegated,
        DelegatedSubscriptionData delegatedParams,
        byte[] commitment
    ) {
        long interval = subscription.getInterval();
        SubscriptionRunKey runKey = new SubscriptionRunKey(id, interval);

        log.info("Processing subscription: id={}, interval={}, delegated={}", id, interval, delegated);

        // Block further processing for this run
        pendingTxs.put(runKey, BLOCKED_TX);

        // TODO: Generate Commitment
    }

}
