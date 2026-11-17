package com.oficina.production.infrastructure.messaging.rabbitmq;

import com.oficina.production.domain.event.ProducaoFalhaEvent;
import com.oficina.production.domain.event.ProducaoFinalizadaEvent;
import com.oficina.production.domain.event.ProducaoIniciadaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Messaging: ProductionRabbitMqPublisher")
class ProductionRabbitMqPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ProductionRabbitMqPublisher publisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "productionExchangeName", "production.events.exchange");
    }

    @Test
    @DisplayName("Deve publicar evento de produção iniciada")
    void devePublicarIniciada() {
        ProducaoIniciadaEvent event = ProducaoIniciadaEvent.builder().osId(1L).numeroOs("OS-1").build();
        publisher.publicarProducaoIniciada(event);
        verify(rabbitTemplate).convertAndSend(eq("production.events.exchange"), eq("production.iniciada"), eq(event));
    }

    @Test
    @DisplayName("Deve publicar evento de produção finalizada")
    void devePublicarFinalizada() {
        ProducaoFinalizadaEvent event = ProducaoFinalizadaEvent.builder().osId(1L).numeroOs("OS-1").build();
        publisher.publicarProducaoFinalizada(event);
        verify(rabbitTemplate).convertAndSend(eq("production.events.exchange"), eq("production.finalizada"), eq(event));
    }

    @Test
    @DisplayName("Deve publicar evento de produção com falha (Saga Rollback)")
    void devePublicarFalha() {
        ProducaoFalhaEvent event = ProducaoFalhaEvent.builder().osId(1L).numeroOs("OS-1").motivo("Erro").build();
        publisher.publicarProducaoFalha(event);
        verify(rabbitTemplate).convertAndSend(eq("production.events.exchange"), eq("production.falha"), eq(event));
    }
}
