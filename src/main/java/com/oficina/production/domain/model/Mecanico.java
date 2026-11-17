package com.oficina.production.domain.model;

import com.oficina.production.domain.exception.BusinessException;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "matricula")
public class Mecanico {

    private String matricula;
    private String nome;
    private String especialidade;

    public void validar() {
        if (nome == null || nome.trim().isEmpty()) {
            throw new BusinessException("Nome do mecânico é obrigatório.");
        }
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new BusinessException("Matrícula do mecânico é obrigatória.");
        }
    }
}
