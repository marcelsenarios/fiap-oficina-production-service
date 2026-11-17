package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.domain.exception.EntityNotFoundException;
import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.model.PrioridadeProducao;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Use Case: Consultar Ordem de Produção")
class ConsultarOrdemProducaoUseCaseTest {

    @Mock
    private OrdemProducaoRepository repository;

    @InjectMocks
    private ConsultarOrdemProducaoUseCase useCase;

    @Test
    @DisplayName("Deve buscar por ID")
    void deveBuscarPorId() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC", "M", "D", PrioridadeProducao.NORMAL);
        ordem.setId("p-1");
        when(repository.buscarPorId("p-1")).thenReturn(Optional.of(ordem));

        OrdemProducaoResponse resp = useCase.buscarPorId("p-1");
        assertNotNull(resp);
        assertEquals("p-1", resp.getId());
    }

    @Test
    @DisplayName("Deve buscar por OS ID")
    void deveBuscarPorOsId() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC", "M", "D", PrioridadeProducao.NORMAL);
        when(repository.buscarPorOsId(1L)).thenReturn(Optional.of(ordem));

        OrdemProducaoResponse resp = useCase.buscarPorOsId(1L);
        assertNotNull(resp);
        assertEquals(1L, resp.getOsId());
    }

    @Test
    @DisplayName("Deve buscar por Número da OS")
    void deveBuscarPorNumeroOs() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC", "M", "D", PrioridadeProducao.NORMAL);
        when(repository.buscarPorNumeroOs("OS-1")).thenReturn(Optional.of(ordem));

        OrdemProducaoResponse resp = useCase.buscarPorNumeroOs("OS-1");
        assertNotNull(resp);
        assertEquals("OS-1", resp.getNumeroOs());
    }

    @Test
    @DisplayName("Deve listar todas as ordens")
    void deveListarTodas() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC", "M", "D", PrioridadeProducao.NORMAL);
        when(repository.listarTodas()).thenReturn(List.of(ordem));

        List<OrdemProducaoResponse> lista = useCase.listarTodas();
        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando não encontrar")
    void deveLancarExcecaoNaoEncontrado() {
        when(repository.buscarPorId("x")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> useCase.buscarPorId("x"));

        when(repository.buscarPorOsId(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> useCase.buscarPorOsId(99L));

        when(repository.buscarPorNumeroOs("OS-99")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> useCase.buscarPorNumeroOs("OS-99"));
    }
}
