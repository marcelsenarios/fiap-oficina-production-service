package com.oficina.production.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para inclusão de apontamento ou diário de bordo técnico")
public class ApontamentoRequest {

    @NotBlank(message = "Autor do apontamento é obrigatório")
    @Schema(description = "Identificação de quem redigiu a nota", example = "Roberto Oliveira")
    private String autor;

    @NotBlank(message = "Descrição é obrigatória")
    @Schema(description = "Relato técnico da operação realizada", example = "Sangria do sistema de freio concluída sem vazamentos.")
    private String descricao;
}
