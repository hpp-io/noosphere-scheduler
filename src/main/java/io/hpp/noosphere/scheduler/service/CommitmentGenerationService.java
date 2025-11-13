package io.hpp.noosphere.scheduler.service;

import io.hpp.noosphere.scheduler.service.blockchain.BlockChainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

/**
 * Service for generating commitments periodically.
 */
@Service
public class CommitmentGenerationService {

    private final Logger log = LoggerFactory.getLogger(CommitmentGenerationService.class);

    private final BlockChainService blockChainService;

    /**
     * Constructor for dependency injection.
     */
    public CommitmentGenerationService(BlockChainService blockChainService) {
        this.blockChainService = blockChainService;
    }

    /**
     * Scheduled task to generate commitments.
     * This method contains the placeholder for the main logic.
     */
    @Scheduled(cron = "${application.scheduleTask.commitment-generation.cron}")
    public void generateCommitment() {
        log.info("Starting commitment generation task...");
        blockChainService.processActiveSubscriptions();
        log.info("Finished commitment generation task.");
    }
}
