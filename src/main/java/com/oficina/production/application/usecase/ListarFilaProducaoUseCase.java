package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.domain.model.StatusProducao;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarFilaProducaoUseCase {

    private final OrdemProducaoRepository repository;

    public List<OrdemProducaoResponse> listarFilaAtiva() {
        return repository.buscarFilaOrdenadaPorPrioridadeEEntrada().stream()
                .map(OrdemProducaoResponse::fromDomain)
                .toList();
    }

    public List<OrdemProducaoResponse> listarPorStatus(StatusProducao status) {
        return repository.buscarPorStatus(status).stream()
                .map(OrdemProducaoResponse::fromDomain)
                .toList();
    }
}
