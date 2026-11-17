package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.FinalizarProducaoRequest;
import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.domain.event.ProducaoFinalizadaEvent;
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
public class FinalizarProducaoUseCase {

    private final OrdemProducaoRepository repository;
    private final ProductionEventPublisherPort eventPublisher;

    public OrdemProducaoResponse executar(String id, FinalizarProducaoRequest request) {
        OrdemProducao ordem = repository.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Produção não encontrada com ID: " + id));

        String notas = request != null ? request.getNotasFinais() : "";
        ordem.finalizar(notas);
        OrdemProducao salva = repository.salvar(ordem);

        // Notifica o OS Service via RabbitMQ para transicionar a OS para FINALIZADA
        ProducaoFinalizadaEvent evento = ProducaoFinalizadaEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .osId(salva.getOsId())
                .numeroOs(salva.getNumeroOs())
                .statusProducao("FINALIZADA")
                .notasFinais(notas)
                .dataFinalizacao(LocalDateTime.now())
                .build();

        eventPublisher.publicarProducaoFinalizada(evento);

        return OrdemProducaoResponse.fromDomain(salva);
    }
}
