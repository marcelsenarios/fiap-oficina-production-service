package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.application.dto.ReportarFalhaRequest;
import com.oficina.production.domain.event.ProducaoFalhaEvent;
import com.oficina.production.domain.event.ProductionEventPublisherPort;
import com.oficina.production.domain.exception.EntityNotFoundException;
import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.model.PrioridadeProducao;
import com.oficina.production.domain.model.StatusProducao;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Use Case: Reportar Falha na Produção (Saga Rollback)")
class ReportarFalhaProducaoUseCaseTest {

    @Mock
    private OrdemProducaoRepository repository;

    @Mock
    private ProductionEventPublisherPort eventPublisher;

    @InjectMocks
    private ReportarFalhaProducaoUseCase useCase;

    @Test
    @DisplayName("Deve reportar falha e disparar compensação no Saga")
    void deveReportarFalhaEDispararCompensacao() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC", "M", "D", PrioridadeProducao.NORMAL);
        ordem.setId("p-1");

        when(repository.buscarPorId("p-1")).thenReturn(Optional.of(ordem));
        when(repository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ReportarFalhaRequest req = ReportarFalhaRequest.builder().motivo("Peça indisponível no mercado").build();
        OrdemProducaoResponse resp = useCase.executar("p-1", req);

        assertNotNull(resp);
        assertEquals(StatusProducao.FALHA_TECNICA, resp.getStatus());
        assertEquals("Peça indisponível no mercado", resp.getMotivoFalha());
        verify(eventPublisher, times(1)).publicarProducaoFalha(any(ProducaoFalhaEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se não encontrado")
    void deveLancarExcecaoNaoEncontrado() {
        when(repository.buscarPorId("p-x")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> useCase.executar("p-x", new ReportarFalhaRequest("erro")));
    }
}
