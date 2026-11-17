package com.oficina.production.infrastructure.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oficina.production.application.dto.EnfileirarOrdemRequest;
import com.oficina.production.application.usecase.CancelarOrdemProducaoUseCase;
import com.oficina.production.application.usecase.EnfileirarOrdemProducaoUseCase;
import com.oficina.production.domain.model.PrioridadeProducao;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import com.oficina.production.infrastructure.messaging.dto.OsCanceladaIntegrationMsg;
import com.oficina.production.infrastructure.messaging.dto.OsStatusAtualizadoIntegrationMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductionOsEventsListener {

    private final EnfileirarOrdemProducaoUseCase enfileirarUseCase;
    private final CancelarOrdemProducaoUseCase cancelarUseCase;
    private final OrdemProducaoRepository repository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queue.production-os-events:production.os.events.queue}")
    public void processarEventoOs(Object payload) {
        log.info("Production Service: Mensagem recebida da fila de OS: {}", payload);

        try {
            if (payload instanceof Map<?, ?> map) {
                if (map.containsKey("statusNovo")) {
                    OsStatusAtualizadoIntegrationMsg statusMsg = objectMapper.convertValue(map, OsStatusAtualizadoIntegrationMsg.class);
                    processarStatusAtualizado(statusMsg);
                } else if (map.containsKey("motivo") && map.containsKey("dataCancelamento")) {
                    OsCanceladaIntegrationMsg canceladaMsg = objectMapper.convertValue(map, OsCanceladaIntegrationMsg.class);
                    processarCancelamento(canceladaMsg);
                }
            } else if (payload instanceof OsStatusAtualizadoIntegrationMsg statusMsg) {
                processarStatusAtualizado(statusMsg);
            } else if (payload instanceof OsCanceladaIntegrationMsg canceladaMsg) {
                processarCancelamento(canceladaMsg);
            }
        } catch (Exception e) {
            log.error("Erro ao processar mensagem da fila de OS no Production Service: {}", e.getMessage(), e);
        }
    }

    private void processarStatusAtualizado(OsStatusAtualizadoIntegrationMsg msg) {
        log.info("Production Service [Saga]: Status da OS #{} atualizado para {}", msg.getNumeroOs(), msg.getStatusNovo());

        // Quando o pagamento for aprovado/OS for para PAGO ou APROVADO, enfileira automaticamente na produção
        if ("PAGO".equalsIgnoreCase(msg.getStatusNovo()) || "APROVADO".equalsIgnoreCase(msg.getStatusNovo())) {
            if (repository.buscarPorOsId(msg.getOsId()).isEmpty()) {
                log.info("Production Service: Enfileirando OS #{} na esteira de produção", msg.getNumeroOs());
                enfileirarUseCase.executar(EnfileirarOrdemRequest.builder()
                        .osId(msg.getOsId())
                        .numeroOs(msg.getNumeroOs())
                        .veiculoPlaca("PLACA-OS-" + msg.getOsId())
                        .veiculoModelo("Veículo da OS " + msg.getNumeroOs())
                        .descricaoServicos("Serviços aprovados e pagos no valor de " + msg.getValorTotal())
                        .prioridade(PrioridadeProducao.NORMAL)
                        .build());
            }
        }
    }

    private void processarCancelamento(OsCanceladaIntegrationMsg msg) {
        log.warn("Production Service [Saga Compensação]: OS #{} cancelada. Cancelando ordem de produção.", msg.getNumeroOs());
        cancelarUseCase.cancelarPorOsId(msg.getOsId(), msg.getMotivo());
    }
}
