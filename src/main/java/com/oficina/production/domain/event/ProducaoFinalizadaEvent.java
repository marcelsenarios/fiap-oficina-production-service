package com.oficina.production.domain.event;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProducaoFinalizadaEvent implements Serializable {
    private String eventId;
    private Long osId;
    private String numeroOs;
    @Builder.Default
    private String statusProducao = "FINALIZADA";
    private String notasFinais;
    private LocalDateTime dataFinalizacao;
}
