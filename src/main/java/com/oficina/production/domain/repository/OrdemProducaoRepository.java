package com.oficina.production.domain.repository;

import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.model.StatusProducao;

import java.util.List;
import java.util.Optional;

public interface OrdemProducaoRepository {

    OrdemProducao salvar(OrdemProducao ordem);

    Optional<OrdemProducao> buscarPorId(String id);

    Optional<OrdemProducao> buscarPorOsId(Long osId);

    Optional<OrdemProducao> buscarPorNumeroOs(String numeroOs);

    List<OrdemProducao> listarTodas();

    List<OrdemProducao> buscarPorStatus(StatusProducao status);

    List<OrdemProducao> buscarFilaOrdenadaPorPrioridadeEEntrada();
}
