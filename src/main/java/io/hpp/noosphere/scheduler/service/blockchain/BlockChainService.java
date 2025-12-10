package io.hpp.noosphere.scheduler.service.blockchain;

import io.hpp.noosphere.scheduler.service.blockchain.dto.OnchainSubscriptionId;
import io.hpp.noosphere.scheduler.service.blockchain.dto.SubscriptionIdentifier;
import io.hpp.noosphere.scheduler.service.blockchain.dto.SubscriptionRunKey;
import io.hpp.noosphere.scheduler.service.blockchain.web3.Web3DelegatorService;
import io.hpp.noosphere.scheduler.service.dto.BaseRequestDTO;
import io.hpp.noosphere.scheduler.service.dto.OnchainRequestDTO;
import io.hpp.noosphere.scheduler.service.dto.SubscriptionDTO;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

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
    private final Set<SubscriptionRunKey> committedIntervals = ConcurrentHashMap.newKeySet();

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
            processOnchainRequest(onchainRequestDTO);
            return CompletableFuture.completedFuture(null);
        } else {
            log.error("Unknown request type to track: {}", request);
            return CompletableFuture.failedFuture(new IllegalArgumentException("Unknown request type"));
        }
    }

    private void processOnchainRequest(OnchainRequestDTO requestDTO) {
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

    private CompletableFuture<ShouldProcessResult> shouldProcess(SubscriptionIdentifier subId, SubscriptionDTO subscription) {
        if (subscription.isCallback() || subscription.isCompleted()) {
            stopTracking(subId);
            return CompletableFuture.completedFuture(new ShouldProcessResult(false, null));
        }
        if (!subscription.isActive() || subscription.getInterval() == 1) {
            return CompletableFuture.completedFuture(new ShouldProcessResult(false, null));
        }

        long interval = subscription.getInterval();
        SubscriptionRunKey runKey = new SubscriptionRunKey(subId, interval);

        if (pendingTxs.containsKey(runKey)) {
            return CompletableFuture.completedFuture(new ShouldProcessResult(false, null)); // Already processing
        }

        // 커밋먼트가 이미 확인된 경우, 불필요한 RPC 호출을 건너뜁니다.
        if (committedIntervals.contains(runKey)) {
            log.trace("Skipping sub {}, interval {}: Commitment already found in cache.", subId, interval);
            return CompletableFuture.completedFuture(new ShouldProcessResult(false, null));
        }

        if (txAttempts.getOrDefault(runKey, new AtomicInteger(0)).get() >= 3) {
            log.warn("Subscription {} has exceeded max retries for interval {}.", subId, interval);
            return CompletableFuture.completedFuture(new ShouldProcessResult(false, null));
        }

        return coordinator
            .hasRequestCommitments(BigInteger.valueOf(subscription.getId()), BigInteger.valueOf(interval))
            .thenCompose(hasCommitment -> {
                if (hasCommitment) {
                    committedIntervals.add(runKey);
                    return CompletableFuture.completedFuture(new ShouldProcessResult(false, null));
                } else {
                    return CompletableFuture.completedFuture(new ShouldProcessResult(true, null));
                }
            })
            .exceptionally(ex -> {
                // Stop tracking if the subscription is cancelled or non-existent
                String errorMsg = ex.getMessage();
                if (errorMsg != null && errorMsg.contains("execution reverted")) {
                    log.warn("Subscription {} appears to be cancelled or non-existent on-chain. Stopping tracking.", subId);
                    stopTracking(subId);
                }
                return new ShouldProcessResult(false, null);
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
                            // compute를 사용하여 runKey에 대한 작업을 원자적으로 처리합니다.
                            txAttempts.compute(runKey, (k, attempts) -> {
                                int currentAttempts = (attempts == null) ? 1 : attempts.incrementAndGet();
                                log.info("Failed tx detected for {}. Attempt #{}", runKey, currentAttempts);
                                if (currentAttempts < 3) {
                                    pendingTxs.remove(runKey); // 재시도를 위해 블록을 해제합니다.
                                } else {
                                    log.error("Max retries reached for {}. It will remain blocked.", runKey);
                                }
                                return attempts == null ? new AtomicInteger(1) : attempts;
                            });
                        }
                    });
            }
        });
    }

    private void stopTracking(SubscriptionIdentifier subscriptionId) {
        subscriptions.remove(((OnchainSubscriptionId) subscriptionId).id());

        pendingTxs.keySet().removeIf(key -> key.subscriptionId().equals(subscriptionId));
        txAttempts.keySet().removeIf(key -> key.subscriptionId().equals(subscriptionId));
        committedIntervals.removeIf(key -> key.subscriptionId().equals(subscriptionId));

        log.info("Stopped tracking subscription: {}", subscriptionId);
    }

    @Async
    public void generatedCommitment(SubscriptionIdentifier id, SubscriptionDTO subscription) {
        long interval = subscription.getInterval();
        SubscriptionRunKey runKey = new SubscriptionRunKey(id, interval);

        log.info("Processing subscription: id={}, interval={}", id, interval);

        // Block further processing for this run
        pendingTxs.put(runKey, BLOCKED_TX);

        coordinator
            .prepareNextInterval(BigInteger.valueOf(subscription.getId()), BigInteger.valueOf(interval), wallet.getAddress())
            .thenAccept(receipt -> {
                if (receipt.isStatusOK()) {
                    log.info(
                        "Successfully prepared next interval for sub {}, interval {}. Tx: {}",
                        id,
                        interval,
                        receipt.getTransactionHash()
                    );
                    // Update with the txHash once the transaction is successfully mined.
                    pendingTxs.put(runKey, receipt.getTransactionHash());
                } else {
                    log.error(
                        "Failed to prepare next interval for sub {}, interval {}. Tx: {}",
                        id,
                        interval,
                        receipt.getTransactionHash()
                    );
                    // Remove 'BLOCKED_TX' on failure to allow for a retry.
                    pendingTxs.remove(runKey);
                }
            })
            .exceptionally(ex -> {
                log.error("Error preparing next interval for sub {}, interval {}", id, interval, ex);

                // Stop tracking if the subscription is cancelled or non-existent
                String errorMsg = ex.getMessage();
                if (errorMsg != null && (errorMsg.contains("execution x") || errorMsg.contains("Transaction simulation failed"))) {
                    log.warn("Subscription {} appears to be cancelled or invalid on-chain. Stopping tracking.", id);
                    stopTracking(id);
                } else {
                    // Remove 'BLOCKED_TX' on failure to allow for a retry.
                    pendingTxs.remove(runKey);
                }
                return null;
            });
    }
}
