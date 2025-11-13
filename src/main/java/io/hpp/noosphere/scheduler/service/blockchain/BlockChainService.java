package io.hpp.noosphere.scheduler.service.blockchain;

import io.hpp.noosphere.scheduler.service.blockchain.dto.*;
import io.hpp.noosphere.scheduler.service.blockchain.web3.Web3DelegatorService;
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
    private final Web3DelegatorService web3DelegatorService;

    private record ShouldProcessResult(boolean should, byte[] commitment) {}

    private final Web3j web3j;
    private final CoordinatorService coordinator;
    private final WalletService wallet;

    // State management using thread-safe collections
    private final Map<Long, SubscriptionDTO> subscriptions = new ConcurrentHashMap<>();
    private final Map<SubscriptionRunKey, String> pendingTxs = new ConcurrentHashMap<>();
    private final Map<SubscriptionRunKey, AtomicInteger> txAttempts = new ConcurrentHashMap<>();

    public BlockChainService(Web3j web3j, CoordinatorService coordinator, WalletService wallet, Web3DelegatorService web3DelegatorService) {
        this.web3j = web3j;
        this.coordinator = coordinator;
        this.wallet = wallet;
        this.web3DelegatorService = web3DelegatorService;
    }

    /**
     * Tracks incoming on-chain messages.
     */
    @Async
    public CompletableFuture<Void> processIncomingRequest(BaseRequestDTO request) {
        if (request instanceof OnchainRequestDTO onchainRequestDTO) {
            ProcessOnchainRequest(onchainRequestDTO);
            return CompletableFuture.completedFuture(null);
        } else {
            log.error("Unknown request type to track: {}", request);
            return CompletableFuture.failedFuture(new IllegalArgumentException("Unknown request type"));
        }
    }

    private void ProcessOnchainRequest(OnchainRequestDTO requestDTO) {
        subscriptions.put(requestDTO.getSubscription().getId(), requestDTO.getSubscription());
        log.info("Tracked new subscription! id={}, total={}", requestDTO.getSubscription().getId(), subscriptions.size());
    }

    public void processActiveSubscriptions() {
        pruneFailedTxs();

        // Process regular subscriptions
        subscriptions.forEach((subId, subscription) -> {
            shouldProcess(new OnchainSubscriptionId(subId), subscription).thenAccept(result -> {
                if (result.should()) {
                    generatedCommitment(new OnchainSubscriptionId(subId), subscription);
                }
            });
        });
    }

    private CompletableFuture<ShouldProcessResult> shouldProcess(
        SubscriptionIdentifier subId,
        SubscriptionDTO subscription
    ) {
        if (subscription.isCallback()) {
            stopTracking(subId);
        }
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

        return coordinator
            .hasRequestCommitments(BigInteger.valueOf(subscription.getId()), BigInteger.valueOf(interval))
            .thenCompose(hasCommitment -> {
                if (hasCommitment) {
                    return coordinator.getCommitment(subscription.getId(), interval)
                        .thenApply(commitment -> new ShouldProcessResult(false, coordinator.encodeCommitment(commitment)));
                } else {
                    return CompletableFuture.completedFuture(new ShouldProcessResult(true, null));
                }
            });

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

    private void stopTracking(SubscriptionIdentifier subscriptionId) {
        subscriptions.remove(((OnchainSubscriptionId) subscriptionId).id());

        pendingTxs.keySet().removeIf(key -> key.subscriptionId().equals(subscriptionId));
        txAttempts.keySet().removeIf(key -> key.subscriptionId().equals(subscriptionId));

        log.info("Stopped tracking subscription: {}", subscriptionId);
    }

    @Async
    public void generatedCommitment(
        SubscriptionIdentifier id,
        SubscriptionDTO subscription
    ) {
        long interval = subscription.getInterval();
        SubscriptionRunKey runKey = new SubscriptionRunKey(id, interval);

        log.info("Processing subscription: id={}, interval={}", id, interval);

        // Block further processing for this run
        pendingTxs.put(runKey, BLOCKED_TX);

        coordinator
            .prepareNextInterval(BigInteger.valueOf(subscription.getId()), BigInteger.valueOf(interval), wallet.getAddress())
            .thenAccept(receipt -> {
                if (receipt.isStatusOK()) {
                    log.info("Successfully prepared next interval for sub {}, interval {}. Tx: {}", id, interval, receipt.getTransactionHash());
                    // Update with the txHash once the transaction is successfully mined.
                    pendingTxs.put(runKey, receipt.getTransactionHash());
                } else {
                    log.error("Failed to prepare next interval for sub {}, interval {}. Tx: {}", id, interval, receipt.getTransactionHash());
                    // Remove 'BLOCKED_TX' on failure to allow for a retry.
                    pendingTxs.remove(runKey);
                }
            })
            .exceptionally(ex -> {
                log.error("Error preparing next interval for sub {}, interval {}", id, interval, ex);
                // Also remove 'BLOCKED_TX' on exception to allow for a retry.
                pendingTxs.remove(runKey);
                return null;
            });
    }

}
