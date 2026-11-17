package com.oficina.production.infrastructure.persistence.document;

import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.model.PrioridadeProducao;
import com.oficina.production.domain.model.StatusProducao;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "ordens_producao")
public class OrdemProducaoDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private Long osId;

    @Indexed
    private String numeroOs;

    private String veiculoPlaca;
    private String veiculoModelo;
    private String descricaoServicos;

    @Indexed
    private PrioridadeProducao prioridade;

    @Indexed
    private StatusProducao status;

    private MecanicoDocument mecanicoResponsavel;

    @Builder.Default
    private List<ApontamentoTecnicoDocument> apontamentos = new ArrayList<>();

    @Indexed
    private LocalDateTime dataEntradaFila;

    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFinalizacao;
    private String motivoFalha;

    public static OrdemProducaoDocument fromDomain(OrdemProducao domain) {
        if (domain == null) return null;
        return OrdemProducaoDocument.builder()
                .id(domain.getId())
                .osId(domain.getOsId())
                .numeroOs(domain.getNumeroOs())
                .veiculoPlaca(domain.getVeiculoPlaca())
                .veiculoModelo(domain.getVeiculoModelo())
                .descricaoServicos(domain.getDescricaoServicos())
                .prioridade(domain.getPrioridade())
                .status(domain.getStatus())
                .mecanicoResponsavel(MecanicoDocument.fromDomain(domain.getMecanicoResponsavel()))
                .apontamentos(domain.getApontamentos() != null
                        ? domain.getApontamentos().stream().map(ApontamentoTecnicoDocument::fromDomain).toList()
                        : new ArrayList<>())
                .dataEntradaFila(domain.getDataEntradaFila())
                .dataInicioExecucao(domain.getDataInicioExecucao())
                .dataFinalizacao(domain.getDataFinalizacao())
                .motivoFalha(domain.getMotivoFalha())
                .build();
    }

    public OrdemProducao toDomain() {
        return OrdemProducao.builder()
                .id(this.id)
                .osId(this.osId)
                .numeroOs(this.numeroOs)
                .veiculoPlaca(this.veiculoPlaca)
                .veiculoModelo(this.veiculoModelo)
                .descricaoServicos(this.descricaoServicos)
                .prioridade(this.prioridade)
                .status(this.status)
                .mecanicoResponsavel(this.mecanicoResponsavel != null ? this.mecanicoResponsavel.toDomain() : null)
                .apontamentos(this.apontamentos != null
                        ? this.apontamentos.stream().map(ApontamentoTecnicoDocument::toDomain).toList()
                        : new ArrayList<>())
                .dataEntradaFila(this.dataEntradaFila)
                .dataInicioExecucao(this.dataInicioExecucao)
                .dataFinalizacao(this.dataFinalizacao)
                .motivoFalha(this.motivoFalha)
                .build();
    }
}
