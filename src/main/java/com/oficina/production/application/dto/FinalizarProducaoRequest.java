package com.oficina.production.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para finalização dos reparos da oficina")
public class FinalizarProducaoRequest {

    @Schema(description = "Observações ou notas finais do mecânico", example = "Veículo testado e aprovado em rodagem. Pressão de pneus conferida.")
    private String notasFinais;
}
