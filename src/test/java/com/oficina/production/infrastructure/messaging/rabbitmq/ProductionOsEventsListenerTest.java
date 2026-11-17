package com.oficina.production.infrastructure.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.production.application.dto.EnfileirarOrdemRequest;
import com.oficina.production.application.usecase.CancelarOrdemProducaoUseCase;
import com.oficina.production.application.usecase.EnfileirarOrdemProducaoUseCase;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import com.oficina.production.infrastructure.messaging.dto.OsCanceladaIntegrationMsg;
import com.oficina.production.infrastructure.messaging.dto.OsStatusAtualizadoIntegrationMsg;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Messaging: ProductionOsEventsListener")
class ProductionOsEventsListenerTest {

    @Mock
    private EnfileirarOrdemProducaoUseCase enfileirarUseCase;

    @Mock
    private CancelarOrdemProducaoUseCase cancelarUseCase;

    @Mock
    private OrdemProducaoRepository repository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ProductionOsEventsListener listener;

    @Test
    @DisplayName("Deve enfileirar ordem quando receber OsStatusAtualizado com status PAGO")
    void deveEnfileirarQuandoPago() {
        OsStatusAtualizadoIntegrationMsg msg = OsStatusAtualizadoIntegrationMsg.builder()
                .osId(5L)
                .numeroOs("OS-5")
                .statusNovo("PAGO")
                .valorTotal(new BigDecimal("500.00"))
                .build();

        when(repository.buscarPorOsId(5L)).thenReturn(Optional.empty());

        listener.processarEventoOs(msg);

        verify(enfileirarUseCase, times(1)).executar(any(EnfileirarOrdemRequest.class));
    }

    @Test
    @DisplayName("Deve cancelar ordem quando receber OsCancelada")
    void deveCancelarQuandoOsCancelada() {
        OsCanceladaIntegrationMsg msg = OsCanceladaIntegrationMsg.builder()
                .osId(5L)
                .numeroOs("OS-5")
                .motivo("Cancelado pelo cliente")
                .build();

        listener.processarEventoOs(msg);

        verify(cancelarUseCase, times(1)).cancelarPorOsId(5L, "Cancelado pelo cliente");
    }

    @Test
    @DisplayName("Deve processar mensagem deserializada como Map")
    void deveProcessarMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("osId", 7L);
        map.put("numeroOs", "OS-7");
        map.put("statusNovo", "PAGO");
        map.put("valorTotal", 300);

        when(repository.buscarPorOsId(7L)).thenReturn(Optional.empty());

        listener.processarEventoOs(map);

        verify(enfileirarUseCase, times(1)).executar(any(EnfileirarOrdemRequest.class));
    }
}
