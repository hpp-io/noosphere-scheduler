package io.hpp.noosphere.scheduler.service.dto;

import io.hpp.noosphere.scheduler.service.dto.enumeration.RequestType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OnchainRequestDTO extends BaseRequestDTO {

    private SubscriptionDTO subscription;

    @Builder.Default
    private RequestType type = RequestType.ON_CHAIN_COMPUTATION;

    @Builder.Default
    private Optional<Boolean> requiresProof = Optional.of(false);
}
