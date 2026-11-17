package com.oficina.production.application.dto;

import com.oficina.production.domain.model.PrioridadeProducao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requisição para entrada de veículo na fila de execução da oficina")
public class EnfileirarOrdemRequest {

    @NotNull(message = "ID da Ordem de Serviço é obrigatório")
    @Schema(description = "Identificador da OS", example = "1")
    private Long osId;

    @NotBlank(message = "Número da OS é obrigatório")
    @Schema(description = "Código da OS", example = "OS-A1B2C3D4")
    private String numeroOs;

    @NotBlank(message = "Placa do veículo é obrigatória")
    @Schema(description = "Placa", example = "BRA2E19")
    private String veiculoPlaca;

    @Schema(description = "Modelo do veículo", example = "Corolla 2.0")
    private String veiculoModelo;

    @Schema(description = "Serviços a executar", example = "Troca de óleo, pastilhas e amortecedores")
    private String descricaoServicos;

    @Schema(description = "Prioridade de execução", example = "NORMAL")
    private PrioridadeProducao prioridade;
}
