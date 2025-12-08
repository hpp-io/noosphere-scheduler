package io.hpp.noosphere.scheduler.service.blockchain;

import static io.hpp.noosphere.scheduler.config.Constants.ZERO_ADDRESS;

import io.hpp.noosphere.scheduler.config.ApplicationProperties;
import io.hpp.noosphere.scheduler.service.blockchain.dto.SignatureParamsDTO;
import io.hpp.noosphere.scheduler.service.blockchain.web3.Web3DelegateeCoordinatorService;
import io.hpp.noosphere.scheduler.service.dto.SubscriptionDTO;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Numeric;

@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    private final Web3j web3j;
    private final Web3DelegateeCoordinatorService coordinatorService;
    private final ApplicationProperties.Chain.Wallet walletProperties;
    private final Credentials credentials;
    private final TransactionManager transactionManager;
    private final TransactionReceiptProcessor transactionReceiptProcessor;

    // Lock to synchronize nonce acquisition
    private final ReentrantLock nonceLock = new ReentrantLock();

    // In-memory nonce counter
    private BigInteger nonce;

    public WalletService(
        Web3j web3j,
        Web3DelegateeCoordinatorService coordinatorService,
        ApplicationProperties applicationProperties,
        Credentials credentials,
        BigInteger chainId
    ) throws IOException {
        this.walletProperties = applicationProperties.getChain().getWallet();
        this.coordinatorService = coordinatorService;
        this.web3j = web3j;
        // Inject Credentials bean directly, removing insecure private key handling
        this.credentials = credentials;
        this.transactionManager = new RawTransactionManager(web3j, credentials, chainId.longValue());
        this.transactionReceiptProcessor = new PollingTransactionReceiptProcessor(web3j, 1000, 15); // Poll every 1s, 15 attempts

        log.info("Initialized WalletService for address: {}", getAddress());
    }

    public String getAddress() {
        return credentials.getAddress();
    }

    public String getPaymentAddress() {
        return walletProperties.getPaymentAddress() != null ? walletProperties.getPaymentAddress() : ZERO_ADDRESS;
    }

    /**
     * Simulates a transaction call and retries on failure.
     * @param contractFunction The contract function to simulate.
     * @param subscription The context subscription for logging.
     * @return A CompletableFuture that completes with true if the simulation was bypassed due to an allowed error, false otherwise.
     */
    private CompletableFuture<Boolean> simulateTransaction(Runnable contractFunction, SubscriptionDTO subscription) {
        return CompletableFuture.supplyAsync(() -> {
            for (int i = 0; i < 3; i++) {
                try {
                    contractFunction.run();
                    // If it runs without throwing, simulation is successful
                    return false;
                } catch (Exception e) {
                    // Check for allowed simulation errors
                    for (String allowedError : walletProperties.getAllowedSimErrors()) {
                        if (e.getMessage().toLowerCase().contains(allowedError.toLowerCase())) {
                            log.warn("Bypassing simulation error for subscription {}: {}", subscription.getId(), e.getMessage());
                            return true; // Bypassed
                        }
                    }
                    // If not an allowed error, log and prepare for retry
                    log.warn(
                        "Transaction simulation failed on attempt {} for subscription {}: {}",
                        i + 1,
                        subscription.getId(),
                        e.getMessage()
                    );
                    if (i == 2) { // Last attempt
                        // In a real scenario, you might want to re-throw a specific exception
                        // For simplicity, we'll re-throw the last caught exception.
                        throw new RuntimeException("Transaction simulation failed after 3 attempts", e);
                    }
                    try {
                        Thread.sleep(500); // Delay before retry
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(interruptedException);
                    }
                }
            }
            // Should not be reached
            throw new IllegalStateException("Simulation loop finished without returning a value.");
        });
    }

    public CompletableFuture<String> deliverCompute(
        SubscriptionDTO subscription,
        byte[] input,
        byte[] output,
        byte[] proof,
        byte[] commitmentData
    ) {
        Runnable simulation = () -> {
            try {
                coordinatorService
                    .reportComputeResult(
                        BigInteger.valueOf(subscription.getInterval()),
                        input,
                        output,
                        proof,
                        commitmentData,
                        getPaymentAddress()
                    )
                    .join(); // .send() on a read-only call simulates it
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        return simulateTransaction(simulation, subscription).thenCompose(skipped -> {
            if (skipped) {
                // If simulation was skipped due to an allowed error, send with a fixed gas limit
                return sendTransactionWithLock(
                    () ->
                        coordinatorService.getReportComputeResultTxData(
                            BigInteger.valueOf(subscription.getInterval()),
                            input,
                            output,
                            proof,
                            commitmentData,
                            getPaymentAddress()
                        ),
                    walletProperties.getMaxGasLimit().longValue()
                );
            } else {
                // Otherwise, let web3j estimate the gas
                return sendTransactionWithLock(
                    () ->
                        coordinatorService.getReportComputeResultTxData(
                            BigInteger.valueOf(subscription.getInterval()),
                            input,
                            output,
                            proof,
                            commitmentData,
                            getPaymentAddress()
                        ),
                    null // Gas limit will be estimated
                );
            }
        });
    }

    public CompletableFuture<String> deliverComputeDelegatee(
        SubscriptionDTO subscription,
        SignatureParamsDTO signature,
        byte[] input,
        byte[] output,
        byte[] proof
    ) {
        Runnable simulation = () -> {
            try {
                coordinatorService
                    .simulateReportDelegatedComputeResult(
                        subscription,
                        signature,
                        BigInteger.valueOf(subscription.getInterval()),
                        input,
                        output,
                        proof,
                        getPaymentAddress()
                    )
                    .join();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        return simulateTransaction(simulation, subscription).thenCompose(skipped -> {
            java.util.function.Supplier<String> txDataSupplier = () ->
                coordinatorService.getReportDelegatedComputeResultTxData(
                    subscription,
                    signature,
                    BigInteger.valueOf(subscription.getInterval()),
                    input,
                    output,
                    proof,
                    getPaymentAddress()
                );

            if (skipped) {
                return sendTransactionWithLock(txDataSupplier, walletProperties.getMaxGasLimit().longValue());
            } else {
                return sendTransactionWithLock(txDataSupplier, null);
            }
        });
    }

    /**
     * A public method to send a transaction with the given data and an optional gas limit.
     *
     * @param data The encoded transaction data.
     * @param gasLimit The gas limit to use, or null to estimate.
     * @return A CompletableFuture containing the transaction hash.
     */
    public CompletableFuture<String> sendTransaction(String data, Long gasLimit) {
        return sendTransactionWithLock(() -> data, gasLimit);
    }

    /**
     * Waits for the transaction receipt for a given transaction hash.
     *
     * @param txHash The hash of the transaction to wait for.
     * @return A CompletableFuture containing the TransactionReceipt.
     */
    public CompletableFuture<TransactionReceipt> waitForReceipt(String txHash) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return transactionReceiptProcessor.waitForTransactionReceipt(txHash);
            } catch (IOException | TransactionException e) {
                log.error("Error waiting for transaction receipt for hash {}", txHash, e);
                throw new RuntimeException("Failed to get transaction receipt", e);
            }
        });
    }

    /**
     * Acquires a lock and sends a transaction to prevent nonce collisions.
     * @param txDataSupplier A supplier for the transaction data payload.
     * @param gasLimit A specific gas limit, or null to let web3j estimate it.
     * @return A CompletableFuture containing the transaction hash.
     */
    private CompletableFuture<String> sendTransactionWithLock(java.util.function.Supplier<String> txDataSupplier, Long gasLimit) {
        // Wrap the entire synchronized logic in a CompletableFuture to maintain the async API contract.
        return CompletableFuture.supplyAsync(() -> {
            // Acquire a lock to ensure the entire process (nonce retrieval, signing, sending, waiting) is atomic.
            // This serializes all transaction submissions from this service instance.
            nonceLock.lock();
            BigInteger nonceToSend = null;
            try {
                nonceToSend = getNextNonce(); // Get the correct next nonce for this transaction.
                String to = coordinatorService.getContractAddress();
                String data = txDataSupplier.get();

                // Simulate the transaction using eth_call to catch reverts early
                try {
                    org.web3j.protocol.core.methods.request.Transaction simulationTx =
                        org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(getAddress(), to, data);
                    org.web3j.protocol.core.methods.response.EthCall response = web3j
                        .ethCall(simulationTx, DefaultBlockParameterName.LATEST)
                        .send();

                    if (response.hasError()) {
                        // 이 메시지에 노드가 반환한 revert 사유가 포함됩니다.
                        throw new IOException("eth_call reverted: " + response.getError().getMessage());
                    }
                    if (response.isReverted()) {
                        // revert 사유를 확인하는 또 다른 방법입니다.
                        throw new IOException("eth_call reverted with reason: " + response.getRevertReason());
                    }
                } catch (IOException e) {
                    // 이제 노드의 에러 메시지에 구체적인 revert 사유가 포함될 것입니다.
                    log.error("Transaction simulation failed for nonce {}. Reason: {}", nonceToSend, e.getMessage());
                    throw new RuntimeException("Transaction simulation failed", e);
                }

                BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
                BigInteger finalGasLimit = BigInteger.valueOf(gasLimit != null ? gasLimit : walletProperties.getMaxGasLimit());

                // Create a raw transaction with the specific nonce
                RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonceToSend,
                    gasPrice,
                    finalGasLimit,
                    to,
                    BigInteger.ZERO, // value
                    data
                );

                // Sign the transaction and send it
                byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                String hexValue = Numeric.toHexString(signedMessage);
                EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

                // Check for errors from the node before getting the hash
                if (ethSendTransaction.hasError()) {
                    throw new IOException(
                        "Node returned an error on eth_sendRawTransaction: " + ethSendTransaction.getError().getMessage()
                    );
                }

                String txHash = ethSendTransaction.getTransactionHash();
                if (txHash == null) {
                    throw new IOException(
                        "Node returned a null transaction hash for nonce " +
                        nonceToSend +
                        ", possibly due to an invalid transaction (e.g., insufficient funds)."
                    );
                }

                log.debug("Transaction sent with nonce {}. Hash: {}", nonceToSend, txHash);

                TransactionReceipt receipt = transactionReceiptProcessor.waitForTransactionReceipt(txHash);

                if (!receipt.isStatusOK()) {
                    throw new TransactionException("Transaction failed on-chain with status: " + receipt.getStatus(), receipt);
                }
                return txHash;
            } catch (Exception e) {
                log.error("Error sending transaction with nonce {}", nonceToSend, e);
                // If any step fails, reset the nonce. This forces the next transaction
                // to re-synchronize with the network, preventing nonce gaps.
                resetNonce();
                throw new RuntimeException("Failed to send transaction", e);
            } finally {
                // Always release the lock when the operation is complete or has failed.
                nonceLock.unlock();
            }
        });
    }

    private BigInteger getNextNonce() {
        try {
            if (nonce == null) {
                // Initialize nonce from the network's pending transaction count.
                nonce = web3j.ethGetTransactionCount(getAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount();
                log.info("Initialized nonce from network: {}", nonce);
            }
            // Atomically get the current nonce and increment it for the next call.
            BigInteger nextNonce = nonce;
            nonce = nonce.add(BigInteger.ONE);
            return nextNonce;
        } catch (IOException e) {
            throw new RuntimeException("Failed to get initial nonce from the network", e);
        }
    }

    private void resetNonce() {
        nonceLock.lock();
        try {
            nonce = null; // Set to null to force re-initialization from the network on the next call
            log.warn("Nonce has been reset due to a transaction failure. It will be re-synced from the network.");
        } finally {
            nonceLock.unlock();
        }
    }
}
