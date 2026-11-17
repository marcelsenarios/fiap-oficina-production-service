package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.domain.exception.EntityNotFoundException;
import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelarOrdemProducaoUseCase {

    private final OrdemProducaoRepository repository;

    public OrdemProducaoResponse executar(String id, String motivo) {
        OrdemProducao ordem = repository.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Produção não encontrada com ID: " + id));

        ordem.cancelar(motivo);
        OrdemProducao salva = repository.salvar(ordem);
        log.warn("Saga: Ordem de Produção ID {} cancelada por motivo: {}", id, motivo);

        return OrdemProducaoResponse.fromDomain(salva);
    }

    public void cancelarPorOsId(Long osId, String motivo) {
        repository.buscarPorOsId(osId).ifPresent(ordem -> {
            ordem.cancelar(motivo);
            repository.salvar(ordem);
            log.warn("Saga: Ordem de Produção da OS #{} cancelada por compensação: {}", osId, motivo);
        });
    }
}
