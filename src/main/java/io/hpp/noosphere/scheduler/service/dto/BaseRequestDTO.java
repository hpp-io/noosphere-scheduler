package io.hpp.noosphere.scheduler.service.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode
public abstract class BaseRequestDTO {

    private UUID id;
    private String clientIp;
}
