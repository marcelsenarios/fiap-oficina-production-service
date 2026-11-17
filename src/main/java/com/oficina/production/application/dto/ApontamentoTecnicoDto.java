package com.oficina.production.application.dto;

import com.oficina.production.domain.model.ApontamentoTecnico;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Apontamento técnico registrado")
public class ApontamentoTecnicoDto {
    private LocalDateTime dataHora;
    private String autor;
    private String descricao;

    public static ApontamentoTecnicoDto fromDomain(ApontamentoTecnico a) {
        if (a == null) return null;
        return ApontamentoTecnicoDto.builder()
                .dataHora(a.getDataHora())
                .autor(a.getAutor())
                .descricao(a.getDescricao())
                .build();
    }
}
