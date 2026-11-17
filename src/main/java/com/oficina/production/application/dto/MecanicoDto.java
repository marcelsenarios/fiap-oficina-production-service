package com.oficina.production.application.dto;

import com.oficina.production.domain.model.Mecanico;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados do mecânico")
public class MecanicoDto {
    private String matricula;
    private String nome;
    private String especialidade;

    public static MecanicoDto fromDomain(Mecanico m) {
        if (m == null) return null;
        return MecanicoDto.builder()
                .matricula(m.getMatricula())
                .nome(m.getNome())
                .especialidade(m.getEspecialidade())
                .build();
    }
}
