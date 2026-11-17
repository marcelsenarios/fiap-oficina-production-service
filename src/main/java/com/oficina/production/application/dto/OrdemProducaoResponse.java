package com.oficina.production.application.dto;

import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.model.PrioridadeProducao;
import com.oficina.production.domain.model.StatusProducao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados detalhados da Ordem de Produção")
public class OrdemProducaoResponse {

    private String id;
    private Long osId;
    private String numeroOs;
    private String veiculoPlaca;
    private String veiculoModelo;
    private String descricaoServicos;
    private PrioridadeProducao prioridade;
    private StatusProducao status;
    private MecanicoDto mecanicoResponsavel;
    private List<ApontamentoTecnicoDto> apontamentos;
    private LocalDateTime dataEntradaFila;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFinalizacao;
    private String motivoFalha;

    public static OrdemProducaoResponse fromDomain(OrdemProducao op) {
        if (op == null) return null;
        return OrdemProducaoResponse.builder()
                .id(op.getId())
                .osId(op.getOsId())
                .numeroOs(op.getNumeroOs())
                .veiculoPlaca(op.getVeiculoPlaca())
                .veiculoModelo(op.getVeiculoModelo())
                .descricaoServicos(op.getDescricaoServicos())
                .prioridade(op.getPrioridade())
                .status(op.getStatus())
                .mecanicoResponsavel(MecanicoDto.fromDomain(op.getMecanicoResponsavel()))
                .apontamentos(op.getApontamentos() != null
                        ? op.getApontamentos().stream().map(ApontamentoTecnicoDto::fromDomain).toList()
                        : List.of())
                .dataEntradaFila(op.getDataEntradaFila())
                .dataInicioExecucao(op.getDataInicioExecucao())
                .dataFinalizacao(op.getDataFinalizacao())
                .motivoFalha(op.getMotivoFalha())
                .build();
    }
}
