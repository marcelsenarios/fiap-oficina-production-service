package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.EnfileirarOrdemRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Use Case: Enfileirar Ordem de Produção")
class EnfileirarOrdemProducaoUseCaseTest {

    @Mock
    private OrdemProducaoRepository repository;

    @InjectMocks
    private EnfileirarOrdemProducaoUseCase useCase;

    @Test
    @DisplayName("Deve enfileirar ordem com sucesso")
    void deveEnfileirarComSucesso() {
        EnfileirarOrdemRequest request = EnfileirarOrdemRequest.builder()
                .osId(10L)
                .numeroOs("OS-10")
                .veiculoPlaca("ABC1234")
                .veiculoModelo("Gol")
                .descricaoServicos("Troca de correia")
                .prioridade(PrioridadeProducao.ALTA)
                .build();

        when(repository.salvar(any(OrdemProducao.class))).thenAnswer(invocation -> {
            OrdemProducao op = invocation.getArgument(0);
            op.setId("mongo-10");
            return op;
        });

        OrdemProducaoResponse response = useCase.executar(request);

        assertNotNull(response);
        assertEquals("mongo-10", response.getId());
        assertEquals(10L, response.getOsId());
        assertEquals(StatusProducao.NA_FILA, response.getStatus());
        verify(repository, times(1)).salvar(any(OrdemProducao.class));
    }
}
