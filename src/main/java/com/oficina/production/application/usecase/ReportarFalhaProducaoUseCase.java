package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.application.dto.ReportarFalhaRequest;
import com.oficina.production.domain.event.ProducaoFalhaEvent;
import com.oficina.production.domain.event.ProductionEventPublisherPort;
import com.oficina.production.domain.exception.EntityNotFoundException;
import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportarFalhaProducaoUseCase {

    private final OrdemProducaoRepository repository;
    private final ProductionEventPublisherPort eventPublisher;

    public OrdemProducaoResponse executar(String id, ReportarFalhaRequest request) {
        OrdemProducao ordem = repository.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Produção não encontrada com ID: " + id));

        ordem.reportarFalha(request.getMotivo());
        OrdemProducao salva = repository.salvar(ordem);

        // Notifica o OS Service e demais interessados para compensação do Saga Pattern (Rollback)
        ProducaoFalhaEvent evento = ProducaoFalhaEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .osId(salva.getOsId())
                .numeroOs(salva.getNumeroOs())
                .statusProducao("FALHA")
                .motivo(request.getMotivo())
                .dataFalha(LocalDateTime.now())
                .build();

        eventPublisher.publicarProducaoFalha(evento);

        return OrdemProducaoResponse.fromDomain(salva);
    }
}
