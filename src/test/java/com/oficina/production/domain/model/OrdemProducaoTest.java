package com.oficina.production.domain.model;

import com.oficina.production.domain.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Domínio: Ordem de Produção")
class OrdemProducaoTest {

    private Mecanico mecanicoValido;

    @BeforeEach
    void setUp() {
        mecanicoValido = Mecanico.builder()
                .matricula("MEC-100")
                .nome("Carlos Engenheiro")
                .especialidade("Freios e Suspensão")
                .build();
    }

    @Test
    @DisplayName("Deve enfileirar ordem de produção com sucesso")
    void deveEnfileirarComSucesso() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-12345", "ABC1D23", "Civic 2.0", "Revisão geral", PrioridadeProducao.ALTA);

        assertNotNull(ordem);
        assertEquals(1L, ordem.getOsId());
        assertEquals("OS-12345", ordem.getNumeroOs());
        assertEquals("ABC1D23", ordem.getVeiculoPlaca());
        assertEquals(StatusProducao.NA_FILA, ordem.getStatus());
        assertEquals(PrioridadeProducao.ALTA, ordem.getPrioridade());
        assertNotNull(ordem.getDataEntradaFila());
        assertTrue(ordem.getApontamentos().isEmpty());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios ao enfileirar ordem")
    void deveValidarCamposObrigatoriosAoEnfileirar() {
        assertThrows(BusinessException.class, () ->
                OrdemProducao.enfileirar(null, "OS-1", "ABC", "M", "D", PrioridadeProducao.NORMAL));

        assertThrows(BusinessException.class, () ->
                OrdemProducao.enfileirar(1L, "", "ABC", "M", "D", PrioridadeProducao.NORMAL));

        assertThrows(BusinessException.class, () ->
                OrdemProducao.enfileirar(1L, "OS-1", null, "M", "D", PrioridadeProducao.NORMAL));
    }

    @Test
    @DisplayName("Deve iniciar diagnóstico com sucesso")
    void deveIniciarDiagnostico() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC1D23", "M", "D", PrioridadeProducao.NORMAL);

        ordem.iniciarDiagnostico(mecanicoValido);

        assertEquals(StatusProducao.EM_DIAGNOSTICO, ordem.getStatus());
        assertEquals(mecanicoValido, ordem.getMecanicoResponsavel());
        assertNotNull(ordem.getDataInicioExecucao());
        assertEquals(1, ordem.getApontamentos().size());
    }

    @Test
    @DisplayName("Deve falhar ao iniciar diagnóstico em ordem fora de NA_FILA ou sem mecânico")
    void deveFalharDiagnosticoInvalido() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC1D23", "M", "D", PrioridadeProducao.NORMAL);

        assertThrows(BusinessException.class, () -> ordem.iniciarDiagnostico(null));

        ordem.iniciarDiagnostico(mecanicoValido);
        assertThrows(BusinessException.class, () -> ordem.iniciarDiagnostico(mecanicoValido));
    }

    @Test
    @DisplayName("Deve progredir ciclo de vida: diagnóstico -> execução -> testes -> finalizado")
    void deveProgredirCicloCompleto() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC1D23", "M", "D", PrioridadeProducao.NORMAL);
        ordem.iniciarDiagnostico(mecanicoValido);

        ordem.iniciarExecucao();
        assertEquals(StatusProducao.EM_EXECUCAO, ordem.getStatus());

        ordem.adicionarApontamento("Carlos", "Troca de pastilhas dianteiras");
        assertEquals(3, ordem.getApontamentos().size());

        ordem.iniciarTestes();
        assertEquals(StatusProducao.EM_TESTES, ordem.getStatus());

        ordem.finalizar("Teste de frenagem aprovado com 100% de eficiência.");
        assertEquals(StatusProducao.FINALIZADO, ordem.getStatus());
        assertNotNull(ordem.getDataFinalizacao());
    }

    @Test
    @DisplayName("Deve reportar falha técnica")
    void deveReportarFalhaTecnica() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC1D23", "M", "D", PrioridadeProducao.NORMAL);
        ordem.iniciarDiagnostico(mecanicoValido);
        ordem.iniciarExecucao();

        ordem.reportarFalha("Componente quebrado durante a montagem");
        assertEquals(StatusProducao.FALHA_TECNICA, ordem.getStatus());
        assertEquals("Componente quebrado durante a montagem", ordem.getMotivoFalha());
        assertNotNull(ordem.getDataFinalizacao());

        assertThrows(BusinessException.class, () -> ordem.reportarFalha("Outro erro"));
    }

    @Test
    @DisplayName("Deve cancelar ordem de produção quando não estiver finalizada")
    void deveCancelarOrdem() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC1D23", "M", "D", PrioridadeProducao.NORMAL);

        ordem.cancelar("Cliente desistiu");
        assertEquals(StatusProducao.CANCELADO, ordem.getStatus());
        assertEquals("Cliente desistiu", ordem.getMotivoFalha());
    }

    @Test
    @DisplayName("Não deve permitir cancelar ou reportar falha em ordem já finalizada")
    void naoDeveCancelarFinalizada() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC1D23", "M", "D", PrioridadeProducao.NORMAL);
        ordem.iniciarDiagnostico(mecanicoValido);
        ordem.iniciarExecucao();
        ordem.iniciarTestes();
        ordem.finalizar("Ok");

        assertThrows(BusinessException.class, () -> ordem.cancelar("Cancelamento tardio"));
        assertThrows(BusinessException.class, () -> ordem.reportarFalha("Falha tardia"));
    }
}
