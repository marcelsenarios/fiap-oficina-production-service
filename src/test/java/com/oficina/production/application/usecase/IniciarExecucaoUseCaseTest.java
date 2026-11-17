package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.domain.event.ProducaoIniciadaEvent;
import com.oficina.production.domain.event.ProductionEventPublisherPort;
import com.oficina.production.domain.exception.EntityNotFoundException;
import com.oficina.production.domain.model.Mecanico;
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
@DisplayName("Testes Use Case: Iniciar Execução")
class IniciarExecucaoUseCaseTest {

    @Mock
    private OrdemProducaoRepository repository;

    @Mock
    private ProductionEventPublisherPort eventPublisher;

    @InjectMocks
    private IniciarExecucaoUseCase useCase;

    @Test
    @DisplayName("Deve iniciar execução e publicar evento no RabbitMQ")
    void deveIniciarExecucaoEPublicarEvento() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC", "M", "D", PrioridadeProducao.NORMAL);
        ordem.setId("p-1");
        ordem.iniciarDiagnostico(Mecanico.builder().matricula("M1").nome("Carlos").build());

        when(repository.buscarPorId("p-1")).thenReturn(Optional.of(ordem));
        when(repository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemProducaoResponse response = useCase.executar("p-1");

        assertNotNull(response);
        assertEquals(StatusProducao.EM_EXECUCAO, response.getStatus());
        verify(eventPublisher, times(1)).publicarProducaoIniciada(any(ProducaoIniciadaEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se ID não existir")
    void deveLancarExcecaoNaoExistente() {
        when(repository.buscarPorId("x")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> useCase.executar("x"));
    }
}
