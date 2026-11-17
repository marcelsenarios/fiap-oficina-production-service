package com.oficina.production.domain.model;

import com.oficina.production.domain.exception.BusinessException;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class OrdemProducao {

    private String id;
    private Long osId;
    private String numeroOs;
    private String veiculoPlaca;
    private String veiculoModelo;
    private String descricaoServicos;
    private PrioridadeProducao prioridade;
    private StatusProducao status;
    private Mecanico mecanicoResponsavel;
    @Builder.Default
    private List<ApontamentoTecnico> apontamentos = new ArrayList<>();
    private LocalDateTime dataEntradaFila;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFinalizacao;
    private String motivoFalha;

    public static OrdemProducao enfileirar(Long osId, String numeroOs, String veiculoPlaca, String veiculoModelo, String descricao, PrioridadeProducao prioridade) {
        if (osId == null) {
            throw new BusinessException("ID da OS é obrigatório.");
        }
        if (numeroOs == null || numeroOs.isBlank()) {
            throw new BusinessException("Número da OS é obrigatório.");
        }
        if (veiculoPlaca == null || veiculoPlaca.isBlank()) {
            throw new BusinessException("Placa do veículo é obrigatória.");
        }

        return OrdemProducao.builder()
                .osId(osId)
                .numeroOs(numeroOs)
                .veiculoPlaca(veiculoPlaca)
                .veiculoModelo(veiculoModelo)
                .descricaoServicos(descricao)
                .prioridade(prioridade != null ? prioridade : PrioridadeProducao.NORMAL)
                .status(StatusProducao.NA_FILA)
                .apontamentos(new ArrayList<>())
                .dataEntradaFila(LocalDateTime.now())
                .build();
    }

    public void iniciarDiagnostico(Mecanico mecanico) {
        if (this.status != StatusProducao.NA_FILA) {
            throw new BusinessException("Só é possível iniciar diagnóstico para ordens com status NA_FILA.");
        }
        if (mecanico == null) {
            throw new BusinessException("Mecânico responsável deve ser informado.");
        }
        mecanico.validar();
        this.mecanicoResponsavel = mecanico;
        this.status = StatusProducao.EM_DIAGNOSTICO;
        this.dataInicioExecucao = LocalDateTime.now();
        adicionarApontamento(mecanico.getNome(), "Iniciado diagnóstico veicular preliminar.");
    }

    public void iniciarExecucao() {
        if (this.status != StatusProducao.EM_DIAGNOSTICO && this.status != StatusProducao.NA_FILA) {
            throw new BusinessException("Transição para EM_EXECUCAO permitida apenas a partir de NA_FILA ou EM_DIAGNOSTICO.");
        }
        this.status = StatusProducao.EM_EXECUCAO;
        if (this.dataInicioExecucao == null) {
            this.dataInicioExecucao = LocalDateTime.now();
        }
        String autor = (this.mecanicoResponsavel != null) ? this.mecanicoResponsavel.getNome() : "Oficina";
        adicionarApontamento(autor, "Iniciada desmontagem e execução dos reparos mecânicos.");
    }

    public void iniciarTestes() {
        if (this.status != StatusProducao.EM_EXECUCAO) {
            throw new BusinessException("Só é possível iniciar testes após a conclusão da execução mecânica.");
        }
        this.status = StatusProducao.EM_TESTES;
        String autor = (this.mecanicoResponsavel != null) ? this.mecanicoResponsavel.getNome() : "Oficina";
        adicionarApontamento(autor, "Reparos físicos finalizados. Veículo em testes de rodagem e validação de qualidade.");
    }

    public void adicionarApontamento(String autor, String descricao) {
        if (this.apontamentos == null) {
            this.apontamentos = new ArrayList<>();
        }
        this.apontamentos.add(ApontamentoTecnico.criar(autor, descricao));
    }

    public void finalizar(String notasFinais) {
        if (this.status != StatusProducao.EM_TESTES && this.status != StatusProducao.EM_EXECUCAO) {
            throw new BusinessException("Ordem de produção deve estar em execução ou testes para ser finalizada.");
        }
        this.status = StatusProducao.FINALIZADO;
        this.dataFinalizacao = LocalDateTime.now();
        String autor = (this.mecanicoResponsavel != null) ? this.mecanicoResponsavel.getNome() : "Oficina";
        adicionarApontamento(autor, (notasFinais != null && !notasFinais.isBlank())
                ? "Produção finalizada com sucesso: " + notasFinais
                : "Produção finalizada com sucesso. Veículo pronto para entrega.");
    }

    public void reportarFalha(String motivo) {
        if (this.status == StatusProducao.FINALIZADO || this.status == StatusProducao.FALHA_TECNICA || this.status == StatusProducao.CANCELADO) {
            throw new BusinessException("Não é possível reportar falha em ordem de produção já encerrada.");
        }
        this.status = StatusProducao.FALHA_TECNICA;
        this.motivoFalha = motivo;
        this.dataFinalizacao = LocalDateTime.now();
        String autor = (this.mecanicoResponsavel != null) ? this.mecanicoResponsavel.getNome() : "Oficina";
        adicionarApontamento(autor, "Falha técnica reportada: " + motivo);
    }

    public void cancelar(String motivo) {
        if (this.status == StatusProducao.FINALIZADO || this.status == StatusProducao.FALHA_TECNICA || this.status == StatusProducao.CANCELADO) {
            throw new BusinessException("Não é possível cancelar uma ordem de produção já encerrada.");
        }
        this.status = StatusProducao.CANCELADO;
        this.motivoFalha = motivo;
        this.dataFinalizacao = LocalDateTime.now();
        adicionarApontamento("Saga Orchestrator", "Cancelada por motivo: " + motivo);
    }
}
