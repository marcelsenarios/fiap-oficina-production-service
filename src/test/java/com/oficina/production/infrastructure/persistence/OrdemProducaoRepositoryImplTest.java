package com.oficina.production.infrastructure.persistence;

import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.model.PrioridadeProducao;
import com.oficina.production.domain.model.StatusProducao;
import com.oficina.production.infrastructure.persistence.document.OrdemProducaoDocument;
import com.oficina.production.infrastructure.persistence.mongo.SpringDataOrdemProducaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Adaptador Persistência: OrdemProducaoRepositoryImpl")
class OrdemProducaoRepositoryImplTest {

    @Mock
    private SpringDataOrdemProducaoRepository springDataRepository;

    @InjectMocks
    private OrdemProducaoRepositoryImpl repository;

    @Test
    @DisplayName("Deve salvar e converter OrdemProducao")
    void deveSalvarEConverter() {
        OrdemProducao ordem = OrdemProducao.enfileirar(10L, "OS-10", "ABC", "M", "D", PrioridadeProducao.ALTA);
        OrdemProducaoDocument doc = OrdemProducaoDocument.fromDomain(ordem);
        doc.setId("mongo-10");

        when(springDataRepository.save(any(OrdemProducaoDocument.class))).thenReturn(doc);

        OrdemProducao salvo = repository.salvar(ordem);

        assertNotNull(salvo);
        assertEquals("mongo-10", salvo.getId());
        assertEquals(10L, salvo.getOsId());
    }

    @Test
    @DisplayName("Deve buscar por ID, OS ID e Número OS")
    void deveBuscarPorCampos() {
        OrdemProducaoDocument doc = OrdemProducaoDocument.builder()
                .id("m-1")
                .osId(1L)
                .numeroOs("OS-1")
                .status(StatusProducao.NA_FILA)
                .prioridade(PrioridadeProducao.NORMAL)
                .build();

        when(springDataRepository.findById("m-1")).thenReturn(Optional.of(doc));
        when(springDataRepository.findByOsId(1L)).thenReturn(Optional.of(doc));
        when(springDataRepository.findByNumeroOs("OS-1")).thenReturn(Optional.of(doc));

        assertTrue(repository.buscarPorId("m-1").isPresent());
        assertTrue(repository.buscarPorOsId(1L).isPresent());
        assertTrue(repository.buscarPorNumeroOs("OS-1").isPresent());
    }

    @Test
    @DisplayName("Deve listar todas e por status")
    void deveListarTodasEPorStatus() {
        OrdemProducaoDocument doc = OrdemProducaoDocument.builder().id("m-1").status(StatusProducao.NA_FILA).build();
        when(springDataRepository.findAll()).thenReturn(List.of(doc));
        when(springDataRepository.findByStatus(StatusProducao.NA_FILA)).thenReturn(List.of(doc));

        assertEquals(1, repository.listarTodas().size());
        assertEquals(1, repository.buscarPorStatus(StatusProducao.NA_FILA).size());
    }

    @Test
    @DisplayName("Deve ordenar fila ativa por prioridade: URGENTE > ALTA > NORMAL > BAIXA")
    void deveOrdenarFilaPorPrioridade() {
        OrdemProducaoDocument docBaixa = OrdemProducaoDocument.builder()
                .id("1").prioridade(PrioridadeProducao.BAIXA).dataEntradaFila(LocalDateTime.now().minusHours(2)).build();
        OrdemProducaoDocument docUrgente = OrdemProducaoDocument.builder()
                .id("2").prioridade(PrioridadeProducao.URGENTE).dataEntradaFila(LocalDateTime.now().minusHours(1)).build();
        OrdemProducaoDocument docAlta = OrdemProducaoDocument.builder()
                .id("3").prioridade(PrioridadeProducao.ALTA).dataEntradaFila(LocalDateTime.now().minusHours(3)).build();

        when(springDataRepository.findAtivasOrderByPrioridadeEData()).thenReturn(List.of(docBaixa, docUrgente, docAlta));

        List<OrdemProducao> resultado = repository.buscarFilaOrdenadaPorPrioridadeEEntrada();

        assertEquals(3, resultado.size());
        assertEquals(PrioridadeProducao.URGENTE, resultado.get(0).getPrioridade());
        assertEquals(PrioridadeProducao.ALTA, resultado.get(1).getPrioridade());
        assertEquals(PrioridadeProducao.BAIXA, resultado.get(2).getPrioridade());
    }
}
