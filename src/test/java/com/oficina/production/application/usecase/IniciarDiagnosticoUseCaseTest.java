package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.IniciarDiagnosticoRequest;
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
@DisplayName("Testes Use Case: Iniciar Diagnóstico")
class IniciarDiagnosticoUseCaseTest {

    @Mock
    private OrdemProducaoRepository repository;

    @InjectMocks
    private IniciarDiagnosticoUseCase useCase;

    @Test
    @DisplayName("Deve iniciar diagnóstico com sucesso")
    void deveIniciarDiagnosticoComSucesso() {
        OrdemProducao ordem = OrdemProducao.enfileirar(1L, "OS-1", "ABC", "M", "D", PrioridadeProducao.NORMAL);
        ordem.setId("p-1");

        when(repository.buscarPorId("p-1")).thenReturn(Optional.of(ordem));
        when(repository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        IniciarDiagnosticoRequest request = IniciarDiagnosticoRequest.builder()
                .matriculaMecanico("MEC-01")
                .nomeMecanico("Silvio")
                .especialidade("Freios")
                .build();

        OrdemProducaoResponse response = useCase.executar("p-1", request);

        assertNotNull(response);
        assertEquals(StatusProducao.EM_DIAGNOSTICO, response.getStatus());
        assertEquals("Silvio", response.getMecanicoResponsavel().getNome());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando ordem não existir")
    void deveLancarExcecaoQuandoNaoExistir() {
        when(repository.buscarPorId("not-found")).thenReturn(Optional.empty());

        IniciarDiagnosticoRequest request = IniciarDiagnosticoRequest.builder()
                .matriculaMecanico("MEC-01")
                .nomeMecanico("Silvio")
                .build();

        assertThrows(EntityNotFoundException.class, () -> useCase.executar("not-found", request));
    }
}
