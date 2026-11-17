package com.oficina.production.infrastructure.persistence.mongo;

import com.oficina.production.domain.model.StatusProducao;
import com.oficina.production.infrastructure.persistence.document.OrdemProducaoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataOrdemProducaoRepository extends MongoRepository<OrdemProducaoDocument, String> {

    Optional<OrdemProducaoDocument> findByOsId(Long osId);

    Optional<OrdemProducaoDocument> findByNumeroOs(String numeroOs);

    List<OrdemProducaoDocument> findByStatus(StatusProducao status);

    @Query("{ 'status': { $in: ['NA_FILA', 'EM_DIAGNOSTICO', 'EM_EXECUCAO', 'EM_TESTES'] } }")
    List<OrdemProducaoDocument> findAtivasOrderByPrioridadeEData();
}
