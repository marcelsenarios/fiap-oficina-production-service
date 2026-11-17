package com.oficina.production.infrastructure.messaging.rabbitmq;

import com.oficina.production.domain.event.ProducaoFalhaEvent;
import com.oficina.production.domain.event.ProducaoFinalizadaEvent;
import com.oficina.production.domain.event.ProducaoIniciadaEvent;
import com.oficina.production.domain.event.ProductionEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductionRabbitMqPublisher implements ProductionEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.production:production.events.exchange}")
    private String productionExchangeName;

    @Override
    public void publicarProducaoIniciada(ProducaoIniciadaEvent event) {
        String routingKey = "production.iniciada";
        log.info("Publicando evento de Produção Iniciada: OS #{} para routingKey: {}", event.getNumeroOs(), routingKey);
        rabbitTemplate.convertAndSend(productionExchangeName, routingKey, event);
    }

    @Override
    public void publicarProducaoFinalizada(ProducaoFinalizadaEvent event) {
        String routingKey = "production.finalizada";
        log.info("Publicando evento de Produção Finalizada: OS #{} para routingKey: {}", event.getNumeroOs(), routingKey);
        rabbitTemplate.convertAndSend(productionExchangeName, routingKey, event);
    }

    @Override
    public void publicarProducaoFalha(ProducaoFalhaEvent event) {
        String routingKey = "production.falha";
        log.warn("Publicando evento de Produção com Falha (Saga Rollback): OS #{} por: {} para routingKey: {}",
                event.getNumeroOs(), event.getMotivo(), routingKey);
        rabbitTemplate.convertAndSend(productionExchangeName, routingKey, event);
    }
}
