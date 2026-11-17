package com.oficina.production.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsStatusAtualizadoIntegrationMsg implements Serializable {

    private String eventId;
    private Long osId;
    private String numeroOs;
    private String statusAnterior;
    private String statusNovo;
    private BigDecimal valorTotal;
    private LocalDateTime dataAtualizacao;
}
