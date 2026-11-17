package com.oficina.production.infrastructure.persistence.document;

import com.oficina.production.domain.model.Mecanico;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MecanicoDocument {

    private String matricula;
    private String nome;
    private String especialidade;

    public static MecanicoDocument fromDomain(Mecanico domain) {
        if (domain == null) return null;
        return MecanicoDocument.builder()
                .matricula(domain.getMatricula())
                .nome(domain.getNome())
                .especialidade(domain.getEspecialidade())
                .build();
    }

    public Mecanico toDomain() {
        return Mecanico.builder()
                .matricula(this.matricula)
                .nome(this.nome)
                .especialidade(this.especialidade)
                .build();
    }
}
