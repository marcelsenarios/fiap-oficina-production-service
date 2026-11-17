package com.oficina.production.domain.model;

import com.oficina.production.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Domínio: Apontamento Técnico")
class ApontamentoTecnicoTest {

    @Test
    @DisplayName("Deve criar apontamento com sucesso")
    void deveCriarApontamentoComSucesso() {
        ApontamentoTecnico apontamento = ApontamentoTecnico.criar("Mecânico João", "Troca das velas concluída");

        assertNotNull(apontamento);
        assertEquals("Mecânico João", apontamento.getAutor());
        assertEquals("Troca das velas concluída", apontamento.getDescricao());
        assertNotNull(apontamento.getDataHora());
    }

    @Test
    @DisplayName("Deve lançar exceção quando autor for inválido")
    void deveLancarExcecaoAutorInvalido() {
        assertThrows(BusinessException.class, () -> ApontamentoTecnico.criar("", "Descrição"));
        assertThrows(BusinessException.class, () -> ApontamentoTecnico.criar(null, "Descrição"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando descrição for inválida")
    void deveLancarExcecaoDescricaoInvalida() {
        assertThrows(BusinessException.class, () -> ApontamentoTecnico.criar("Autor", ""));
        assertThrows(BusinessException.class, () -> ApontamentoTecnico.criar("Autor", null));
    }
}
