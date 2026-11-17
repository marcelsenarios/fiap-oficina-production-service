package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.EnfileirarOrdemRequest;
import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnfileirarOrdemProducaoUseCase {

    private final OrdemProducaoRepository repository;

    public OrdemProducaoResponse executar(EnfileirarOrdemRequest request) {
        OrdemProducao ordem = OrdemProducao.enfileirar(
                request.getOsId(),
                request.getNumeroOs(),
                request.getVeiculoPlaca(),
                request.getVeiculoModelo(),
                request.getDescricaoServicos(),
                request.getPrioridade()
        );

        OrdemProducao salva = repository.salvar(ordem);
        return OrdemProducaoResponse.fromDomain(salva);
    }
}
