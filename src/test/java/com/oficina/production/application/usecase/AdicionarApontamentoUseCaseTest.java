package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.ApontamentoRequest;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Use Case: Adicionar Apontamento")
class AdicionarApontamentoUseCaseTest {

    @Mock
    private OrdemProducaoRepository repository;

    @InjectMocks
    private AdicionarApontamentoUseCase useCase;

    @Test
    @DisplayName("Deve adicionar apontamento técnico")
    void deveAdicionarApontamento() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC", "M", "D", PrioridadeProducao.NORMAL);
        ordem.setId("p-1");

        when(repository.buscarPorId("p-1")).thenReturn(Optional.of(ordem));
        when(repository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ApontamentoRequest req = ApontamentoRequest.builder()
                .autor("Mecânico")
                .descricao("Óleo drenado")
                .build();

        OrdemProducaoResponse resp = useCase.executar("p-1", req);

        assertNotNull(resp);
        assertEquals(1, resp.getApontamentos().size());
        assertEquals("Óleo drenado", resp.getApontamentos().get(0).getDescricao());
    }

    @Test
    @DisplayName("Deve lançar exceção se ID não existir")
    void deveLancarExcecaoNaoExistente() {
        when(repository.buscarPorId("p-x")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> useCase.executar("p-x", new ApontamentoRequest()));
    }
}
