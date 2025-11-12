package io.hpp.noosphere.scheduler.service.blockchain.dto;

import io.hpp.noosphere.scheduler.service.dto.SubscriptionDTO;

import java.util.Map;

/**
 * Holds all the necessary data for processing a delegated subscription.
 */
public record DelegatedSubscriptionData(SubscriptionDTO subscription, SignatureParamsDTO signature, Map<String, Object> data) {}
