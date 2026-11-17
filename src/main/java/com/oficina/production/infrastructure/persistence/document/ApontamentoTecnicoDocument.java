package com.oficina.production.infrastructure.persistence.document;

import com.oficina.production.domain.model.ApontamentoTecnico;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApontamentoTecnicoDocument {

    private LocalDateTime dataHora;
    private String autor;
    private String descricao;

    public static ApontamentoTecnicoDocument fromDomain(ApontamentoTecnico domain) {
        if (domain == null) return null;
        return ApontamentoTecnicoDocument.builder()
                .dataHora(domain.getDataHora())
                .autor(domain.getAutor())
                .descricao(domain.getDescricao())
                .build();
    }

    public ApontamentoTecnico toDomain() {
        return ApontamentoTecnico.builder()
                .dataHora(this.dataHora)
                .autor(this.autor)
                .descricao(this.descricao)
                .build();
    }
}
