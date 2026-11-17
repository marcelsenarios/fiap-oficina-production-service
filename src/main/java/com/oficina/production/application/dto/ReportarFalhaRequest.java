package com.oficina.production.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para apontamento de falha ou impossibilidade técnica (Rollback Saga)")
public class ReportarFalhaRequest {

    @NotBlank(message = "Motivo da falha é obrigatório")
    @Schema(description = "Explicação da falha técnica ou falta de componente", example = "Bloco do motor trincado, inviável prosseguir com o reparo previsto.")
    private String motivo;
}
