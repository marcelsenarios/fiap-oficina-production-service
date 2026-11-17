package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.domain.exception.EntityNotFoundException;
import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarOrdemProducaoUseCase {

    private final OrdemProducaoRepository repository;

    public OrdemProducaoResponse buscarPorId(String id) {
        return repository.buscarPorId(id)
                .map(OrdemProducaoResponse::fromDomain)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Produção não encontrada com ID: " + id));
    }

    public OrdemProducaoResponse buscarPorOsId(Long osId) {
        return repository.buscarPorOsId(osId)
                .map(OrdemProducaoResponse::fromDomain)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Produção não encontrada para OS #" + osId));
    }

    public OrdemProducaoResponse buscarPorNumeroOs(String numeroOs) {
        return repository.buscarPorNumeroOs(numeroOs)
                .map(OrdemProducaoResponse::fromDomain)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Produção não encontrada para número de OS: " + numeroOs));
    }

    public List<OrdemProducaoResponse> listarTodas() {
        return repository.listarTodas().stream()
                .map(OrdemProducaoResponse::fromDomain)
                .toList();
    }
}
