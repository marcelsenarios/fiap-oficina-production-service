package com.oficina.production.infrastructure.persistence;

import com.oficina.production.domain.model.OrdemProducao;
import com.oficina.production.domain.model.PrioridadeProducao;
import com.oficina.production.domain.model.StatusProducao;
import com.oficina.production.domain.repository.OrdemProducaoRepository;
import com.oficina.production.infrastructure.persistence.document.OrdemProducaoDocument;
import com.oficina.production.infrastructure.persistence.mongo.SpringDataOrdemProducaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrdemProducaoRepositoryImpl implements OrdemProducaoRepository {

    private final SpringDataOrdemProducaoRepository springDataRepository;

    @Override
    public OrdemProducao salvar(OrdemProducao ordem) {
        OrdemProducaoDocument document = OrdemProducaoDocument.fromDomain(ordem);
        OrdemProducaoDocument salvo = springDataRepository.save(document);
        return salvo.toDomain();
    }

    @Override
    public Optional<OrdemProducao> buscarPorId(String id) {
        return springDataRepository.findById(id).map(OrdemProducaoDocument::toDomain);
    }

    @Override
    public Optional<OrdemProducao> buscarPorOsId(Long osId) {
        return springDataRepository.findByOsId(osId).map(OrdemProducaoDocument::toDomain);
    }

    @Override
    public Optional<OrdemProducao> buscarPorNumeroOs(String numeroOs) {
        return springDataRepository.findByNumeroOs(numeroOs).map(OrdemProducaoDocument::toDomain);
    }

    @Override
    public List<OrdemProducao> listarTodas() {
        return springDataRepository.findAll().stream()
                .map(OrdemProducaoDocument::toDomain)
                .toList();
    }

    @Override
    public List<OrdemProducao> buscarPorStatus(StatusProducao status) {
        return springDataRepository.findByStatus(status).stream()
                .map(OrdemProducaoDocument::toDomain)
                .toList();
    }

    @Override
    public List<OrdemProducao> buscarFilaOrdenadaPorPrioridadeEEntrada() {
        List<OrdemProducaoDocument> ativas = springDataRepository.findAtivasOrderByPrioridadeEData();
        return ativas.stream()
                .map(OrdemProducaoDocument::toDomain)
                .sorted(Comparator.comparingInt(this::getPesoPrioridade)
                        .thenComparing(OrdemProducao::getDataEntradaFila, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    private int getPesoPrioridade(OrdemProducao ordem) {
        if (ordem.getPrioridade() == null) return 3;
        return switch (ordem.getPrioridade()) {
            case URGENTE -> 1;
            case ALTA -> 2;
            case NORMAL -> 3;
            case BAIXA -> 4;
        };
    }
}
