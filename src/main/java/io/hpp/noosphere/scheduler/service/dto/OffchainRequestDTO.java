package io.hpp.noosphere.scheduler.service.dto;

import io.hpp.noosphere.scheduler.service.dto.enumeration.RequestType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OffchainRequestDTO extends BaseRequestDTO {

    private List<String> containers;
    private Map<String, Object> data;

    @Builder.Default
    private RequestType type = RequestType.OFF_CHAIN_COMPUTATION;

    @Builder.Default
    private Boolean requiresProof = false;
}
