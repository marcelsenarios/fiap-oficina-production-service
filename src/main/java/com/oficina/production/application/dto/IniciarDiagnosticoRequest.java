package com.oficina.production.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para atribuição de mecânico e início de diagnóstico")
public class IniciarDiagnosticoRequest {

    @NotBlank(message = "Matrícula do mecânico é obrigatória")
    @Schema(description = "Matrícula interna", example = "MEC-01")
    private String matriculaMecanico;

    @NotBlank(message = "Nome do mecânico é obrigatório")
    @Schema(description = "Nome do mecânico", example = "Roberto Oliveira")
    private String nomeMecanico;

    @Schema(description = "Especialidade técnica", example = "Suspensão e Freios")
    private String especialidade;
}
