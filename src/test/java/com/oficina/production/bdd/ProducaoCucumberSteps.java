package com.oficina.production.bdd;

import com.oficina.production.domain.model.Mecanico;
import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.model.PrioridadeProducao;
import com.oficina.production.domain.model.StatusProducao;
import io.cucumber.java.pt.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProducaoCucumberSteps {

    private Long osId;
    private String numeroOs;
    private String veiculoPlaca;
    private String veiculoModelo;
    private OrdemProducao ordemProducao;
    private Mecanico mecanico;

    @Dado("que uma ordem de produção foi enfileirada para a OS de ID {int} e número {string}")
    public void queUmaOrdemDeProducaoFoiEnfileirada(Integer id, String numero) {
        this.osId = id.longValue();
        this.numeroOs = numero;
    }

    @Dado("a placa do veículo é {string} e modelo {string}")
    public void aPlacaDoVeiculoE(String placa, String modelo) {
        this.veiculoPlaca = placa;
        this.veiculoModelo = modelo;
        this.ordemProducao = OrdemProducao.enfileirar(
                this.osId,
                this.numeroOs,
                this.veiculoPlaca,
                this.veiculoModelo,
                "Serviços mecânicos gerais",
                PrioridadeProducao.NORMAL
        );
    }

    @Quando("o mecânico {string} com matrícula {string} inicia o diagnóstico veicular")
    public void oMecanicoIniciaODiagnostico(String nome, String matricula) {
        this.mecanico = Mecanico.builder()
                .matricula(matricula)
                .nome(nome)
                .especialidade("Mecânica Geral")
                .build();
        this.ordemProducao.iniciarDiagnostico(this.mecanico);
    }

    @Quando("o mecânico inicia a execução física dos reparos")
    public void oMecanicoIniciaAExecucao() {
        this.ordemProducao.iniciarExecucao();
    }

    @Quando("o mecânico adiciona o apontamento técnico {string}")
    public void oMecanicoAdicionaOApontamento(String descricao) {
        this.ordemProducao.adicionarApontamento(this.mecanico.getNome(), descricao);
    }

    @Quando("o mecânico inicia os testes de qualidade")
    public void oMecanicoIniciaOsTestes() {
        this.ordemProducao.iniciarTestes();
    }

    @Quando("o mecânico finaliza a produção com a nota {string}")
    public void oMecanicoFinalizaAProducao(String nota) {
        this.ordemProducao.finalizar(nota);
    }

    @Quando("o mecânico reporta uma falha técnica com o motivo {string}")
    public void oMecanicoReportaUmaFalhaTecnica(String motivo) {
        this.ordemProducao.reportarFalha(motivo);
    }

    @Então("o status da ordem de produção deve ser {string}")
    public void oStatusDaOrdemDeProducaoDeveSer(String statusEsperado) {
        assertEquals(StatusProducao.valueOf(statusEsperado), this.ordemProducao.getStatus());
    }

    @Então("o status final da ordem de produção deve ser {string}")
    public void oStatusFinalDaOrdemDeProducaoDeveSer(String statusEsperado) {
        assertEquals(StatusProducao.valueOf(statusEsperado), this.ordemProducao.getStatus());
    }

    @Então("a ordem de produção deve conter {int} apontamentos registrados")
    public void aOrdemDeProducaoDeveConterApontamentosRegistrados(Integer quantidade) {
        assertEquals(quantidade.intValue(), this.ordemProducao.getApontamentos().size());
    }

    @Então("o motivo registrado da falha deve ser {string}")
    public void oMotivoRegistradoDaFalhaDeveSer(String motivoEsperado) {
        assertEquals(motivoEsperado, this.ordemProducao.getMotivoFalha());
    }
}
