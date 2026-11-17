package com.oficina.production.domain.event;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProducaoFalhaEvent implements Serializable {
    private String eventId;
    private Long osId;
    private String numeroOs;
    @Builder.Default
    private String statusProducao = "FALHA";
    private String motivo;
    private LocalDateTime dataFalha;
}
