package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.domain.event.ProducaoIniciadaEvent;
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
public class IniciarExecucaoUseCase {

    private final OrdemProducaoRepository repository;
    private final ProductionEventPublisherPort eventPublisher;

    public OrdemProducaoResponse executar(String id) {
        OrdemProducao ordem = repository.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Produção não encontrada com ID: " + id));

        ordem.iniciarExecucao();
        OrdemProducao salva = repository.salvar(ordem);

        // Notifica o OS Service via RabbitMQ para transicionar a OS para EM_EXECUCAO
        ProducaoIniciadaEvent evento = ProducaoIniciadaEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .osId(salva.getOsId())
                .numeroOs(salva.getNumeroOs())
                .statusProducao(salva.getStatus().name())
                .mecanicoResponsavel(salva.getMecanicoResponsavel() != null ? salva.getMecanicoResponsavel().getNome() : "Oficina")
                .dataInicio(LocalDateTime.now())
                .build();

        eventPublisher.publicarProducaoIniciada(evento);

        return OrdemProducaoResponse.fromDomain(salva);
    }
}
