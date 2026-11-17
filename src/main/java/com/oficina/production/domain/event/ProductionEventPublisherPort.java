package com.oficina.production.domain.event;

public interface ProductionEventPublisherPort {

    void publicarProducaoIniciada(ProducaoIniciadaEvent event);

    void publicarProducaoFinalizada(ProducaoFinalizadaEvent event);

    void publicarProducaoFalha(ProducaoFalhaEvent event);
}
