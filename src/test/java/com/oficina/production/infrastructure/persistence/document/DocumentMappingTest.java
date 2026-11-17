package com.oficina.production.infrastructure.persistence.document;

import com.oficina.production.domain.model.ApontamentoTecnico;
import com.oficina.production.domain.model.Mecanico;
import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.model.PrioridadeProducao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Mapeamento Documentos Mongo")
class DocumentMappingTest {

    @Test
    @DisplayName("Deve mapear ApontamentoTecnicoDocument de e para domínio")
    void deveMapearApontamentoTecnicoDocument() {
        ApontamentoTecnico domain = ApontamentoTecnico.criar("Carlos", "Troca efetuada");
        ApontamentoTecnicoDocument doc = ApontamentoTecnicoDocument.fromDomain(domain);

        assertNotNull(doc);
        assertEquals("Carlos", doc.getAutor());
        assertEquals("Troca efetuada", doc.getDescricao());

        ApontamentoTecnico back = doc.toDomain();
        assertEquals("Carlos", back.getAutor());

        assertNull(ApontamentoTecnicoDocument.fromDomain(null));
    }

    @Test
    @DisplayName("Deve mapear MecanicoDocument de e para domínio")
    void deveMapearMecanicoDocument() {
        Mecanico domain = Mecanico.builder().matricula("M-1").nome("João").especialidade("Freios").build();
        MecanicoDocument doc = MecanicoDocument.fromDomain(domain);

        assertNotNull(doc);
        assertEquals("M-1", doc.getMatricula());
        assertEquals("João", doc.getNome());

        Mecanico back = doc.toDomain();
        assertEquals("M-1", back.getMatricula());

        assertNull(MecanicoDocument.fromDomain(null));
    }

    @Test
    @DisplayName("Deve mapear OrdemProducaoDocument de e para domínio")
    void deveMapearOrdemProducaoDocument() {
        OrdemProducao op = OrdemProducao.enfileirar(1L, "OS-1", "ABC1D23", "Corolla", "Revisão", PrioridadeProducao.ALTA);
        op.setId("mongo-1");
        op.iniciarDiagnostico(Mecanico.builder().matricula("M1").nome("José").build());

        OrdemProducaoDocument doc = OrdemProducaoDocument.fromDomain(op);
        assertNotNull(doc);
        assertEquals("mongo-1", doc.getId());

        OrdemProducao back = doc.toDomain();
        assertEquals("mongo-1", back.getId());
        assertEquals(1L, back.getOsId());

        assertNull(OrdemProducaoDocument.fromDomain(null));
    }
}
