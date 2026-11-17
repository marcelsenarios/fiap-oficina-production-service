package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.OrdemProducaoResponse;
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
@DisplayName("Testes Use Case: Cancelar Ordem de Produção")
class CancelarOrdemProducaoUseCaseTest {

    @Mock
    private OrdemProducaoRepository repository;

    @InjectMocks
    private CancelarOrdemProducaoUseCase useCase;

    @Test
    @DisplayName("Deve cancelar por ID")
    void deveCancelarPorId() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC", "M", "D", PrioridadeProducao.NORMAL);
        ordem.setId("p-1");

        when(repository.buscarPorId("p-1")).thenReturn(Optional.of(ordem));
        when(repository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemProducaoResponse resp = useCase.executar("p-1", "Cliente desistiu");

        assertNotNull(resp);
        assertEquals(StatusProducao.CANCELADO, resp.getStatus());
    }

    @Test
    @DisplayName("Deve cancelar por OS ID (Saga compensação)")
    void deveCancelarPorOsId() {
        OrdemProducao ordem = OrdemProducao.enfileirar(50L, "OS-50", "ABC", "M", "D", PrioridadeProducao.NORMAL);
        when(repository.buscarPorOsId(50L)).thenReturn(Optional.of(ordem));

        useCase.cancelarPorOsId(50L, "Compensação de Saga");

        verify(repository, times(1)).salvar(ordem);
        assertEquals(StatusProducao.CANCELADO, ordem.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção se não encontrado por ID")
    void deveLancarExcecaoNaoEncontrado() {
        when(repository.buscarPorId("p-x")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> useCase.executar("p-x", "motivo"));
    }
}
