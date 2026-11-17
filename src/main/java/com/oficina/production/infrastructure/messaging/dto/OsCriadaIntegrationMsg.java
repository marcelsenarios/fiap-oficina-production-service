package com.oficina.production.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsCriadaIntegrationMsg implements Serializable {

    private String eventId;
    private Long osId;
    private String numeroOs;
    private String clienteNome;
    private String clienteCpfCnpj;
    private String clienteEmail;
    private String clienteTelefone;
    private String veiculoPlaca;
    private String veiculoModelo;
    private String descricaoProblema;
    private LocalDateTime dataCriacao;
}
