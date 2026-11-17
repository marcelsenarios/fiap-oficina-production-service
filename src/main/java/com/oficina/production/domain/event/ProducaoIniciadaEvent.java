package com.oficina.production.domain.event;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProducaoIniciadaEvent implements Serializable {
    private String eventId;
    private Long osId;
    private String numeroOs;
    private String statusProducao;
    private String mecanicoResponsavel;
    private LocalDateTime dataInicio;
}
