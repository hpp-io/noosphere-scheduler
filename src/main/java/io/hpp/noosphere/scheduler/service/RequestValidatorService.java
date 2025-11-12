package io.hpp.noosphere.scheduler.service;

import io.hpp.noosphere.scheduler.service.dto.DelegatedRequestDTO;
import io.hpp.noosphere.scheduler.service.dto.OffchainRequestDTO;
import io.hpp.noosphere.scheduler.service.dto.OnchainRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RequestValidatorService {

    private static final Logger LOG = LoggerFactory.getLogger(RequestValidatorService.class);

    public boolean validateOffChainRequest(OffchainRequestDTO requestDTO) {
        return true;
    }

    public boolean validateDelegatedRequest(DelegatedRequestDTO requestDTO) {
        return true;
    }

    public boolean validateOnChainRequest(OnchainRequestDTO requestDTO) {
        return true;
    }
}
