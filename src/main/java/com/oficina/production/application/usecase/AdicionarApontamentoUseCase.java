package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.ApontamentoRequest;
import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.domain.exception.EntityNotFoundException;
import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdicionarApontamentoUseCase {

    private final OrdemProducaoRepository repository;

    public OrdemProducaoResponse executar(String id, ApontamentoRequest request) {
        OrdemProducao ordem = repository.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Produção não encontrada com ID: " + id));

        ordem.adicionarApontamento(request.getAutor(), request.getDescricao());
        OrdemProducao salva = repository.salvar(ordem);

        return OrdemProducaoResponse.fromDomain(salva);
    }
}
