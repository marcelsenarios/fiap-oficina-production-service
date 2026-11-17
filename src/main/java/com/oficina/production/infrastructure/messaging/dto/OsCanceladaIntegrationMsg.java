package com.oficina.production.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsCanceladaIntegrationMsg implements Serializable {

    private String eventId;
    private Long osId;
    private String numeroOs;
    private String motivo;
    private LocalDateTime dataCancelamento;
}
