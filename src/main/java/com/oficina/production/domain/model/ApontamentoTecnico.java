package com.oficina.production.domain.model;

import com.oficina.production.domain.exception.BusinessException;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApontamentoTecnico {

    private LocalDateTime dataHora;
    private String autor;
    private String descricao;

    public static ApontamentoTecnico criar(String autor, String descricao) {
        if (autor == null || autor.trim().isEmpty()) {
            throw new BusinessException("Autor do apontamento é obrigatório.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new BusinessException("Descrição do apontamento é obrigatória.");
        }
        return ApontamentoTecnico.builder()
                .dataHora(LocalDateTime.now())
                .autor(autor)
                .descricao(descricao)
                .build();
    }
}
