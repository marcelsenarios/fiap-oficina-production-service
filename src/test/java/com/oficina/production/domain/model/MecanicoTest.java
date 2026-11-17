package com.oficina.production.domain.model;

import com.oficina.production.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Domínio: Mecânico")
class MecanicoTest {

    @Test
    @DisplayName("Deve validar mecânico com sucesso quando dados válidos")
    void deveValidarComSucesso() {
        Mecanico mecanico = Mecanico.builder()
                .matricula("MEC-01")
                .nome("José Silva")
                .especialidade("Motor e Injeção")
                .build();

        assertDoesNotThrow(mecanico::validar);
        assertEquals("MEC-01", mecanico.getMatricula());
        assertEquals("José Silva", mecanico.getNome());
        assertEquals("Motor e Injeção", mecanico.getEspecialidade());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do mecânico for nulo ou vazio")
    void deveLancarExcecaoNomeInvalido() {
        Mecanico m1 = Mecanico.builder().matricula("MEC-01").nome("").build();
        assertThrows(BusinessException.class, m1::validar);

        Mecanico m2 = Mecanico.builder().matricula("MEC-01").nome(null).build();
        assertThrows(BusinessException.class, m2::validar);
    }

    @Test
    @DisplayName("Deve lançar exceção quando matrícula do mecânico for nula ou vazia")
    void deveLancarExcecaoMatriculaInvalida() {
        Mecanico m1 = Mecanico.builder().matricula("").nome("José").build();
        assertThrows(BusinessException.class, m1::validar);

        Mecanico m2 = Mecanico.builder().matricula(null).nome("José").build();
        assertThrows(BusinessException.class, m2::validar);
    }
}
