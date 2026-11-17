package com.oficina.production.application.usecase;

import com.oficina.production.application.dto.IniciarDiagnosticoRequest;
import com.oficina.production.application.dto.OrdemProducaoResponse;
import com.oficina.production.domain.exception.EntityNotFoundException;
import com.oficina.production.domain.model.Mecanico;
import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IniciarDiagnosticoUseCase {

    private final OrdemProducaoRepository repository;

    public OrdemProducaoResponse executar(String id, IniciarDiagnosticoRequest request) {
        OrdemProducao ordem = repository.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Produção não encontrada com ID: " + id));

        Mecanico mecanico = Mecanico.builder()
                .matricula(request.getMatriculaMecanico())
                .nome(request.getNomeMecanico())
                .especialidade(request.getEspecialidade())
                .build();

        ordem.iniciarDiagnostico(mecanico);
        OrdemProducao salva = repository.salvar(ordem);

        return OrdemProducaoResponse.fromDomain(salva);
    }
}
