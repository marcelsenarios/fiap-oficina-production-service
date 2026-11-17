package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.OrdemProducaoResponse;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Use Case: Listar Fila de Produção")
class ListarFilaProducaoUseCaseTest {

    @Mock
    private OrdemProducaoRepository repository;

    @InjectMocks
    private ListarFilaProducaoUseCase useCase;

    @Test
    @DisplayName("Deve listar fila ativa ordenada")
    void deveListarFilaAtiva() {
        OrdemProducao op1 = OrdemProducao.enfileirar(1L, "OS-1", "ABC", "M", "D", PrioridadeProducao.URGENTE);
        OrdemProducao op2 = OrdemProducao.enfileirar(2L, "OS-2", "DEF", "M", "D", PrioridadeProducao.NORMAL);

        when(repository.buscarFilaOrdenadaPorPrioridadeEEntrada()).thenReturn(List.of(op1, op2));

        List<OrdemProducaoResponse> resultado = useCase.listarFilaAtiva();
        assertEquals(2, resultado.size());
        assertEquals("OS-1", resultado.get(0).getNumeroOs());
    }

    @Test
    @DisplayName("Deve listar por status")
    void deveListarPorStatus() {
        OrdemProducao op = OrdemProducao.enfileirar(1L, "OS-1", "ABC", "M", "D", PrioridadeProducao.NORMAL);
        when(repository.buscarPorStatus(StatusProducao.NA_FILA)).thenReturn(List.of(op));

        List<OrdemProducaoResponse> resultado = useCase.listarPorStatus(StatusProducao.NA_FILA);
        assertEquals(1, resultado.size());
    }
}
