package com.oficina.production.infrastructure.api.controller;

import com.oficina.production.application.dto.*;
import com.oficina.production.application.usecase.*;
import com.oficina.production.domain.model.StatusProducao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/producao/ordens")
@RequiredArgsConstructor
@Tag(name = "Produção e Execução", description = "Endpoints para acompanhamento e controle da esteira mecânica de serviços")
public class OrdemProducaoController {

    private final EnfileirarOrdemProducaoUseCase enfileirarUseCase;
    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final IniciarExecucaoUseCase iniciarExecucaoUseCase;
    private final IniciarTestesUseCase iniciarTestesUseCase;
    private final AdicionarApontamentoUseCase adicionarApontamentoUseCase;
    private final FinalizarProducaoUseCase finalizarProducaoUseCase;
    private final ReportarFalhaProducaoUseCase reportarFalhaUseCase;
    private final CancelarOrdemProducaoUseCase cancelarUseCase;
    private final ConsultarOrdemProducaoUseCase consultarUseCase;
    private final ListarFilaProducaoUseCase listarFilaUseCase;

    @PostMapping
    @Operation(summary = "Enfileirar veículo na esteira de produção")
    public ResponseEntity<OrdemProducaoResponse> enfileirar(@Valid @RequestBody EnfileirarOrdemRequest request) {
        OrdemProducaoResponse response = enfileirarUseCase.executar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as ordens de produção ou filtrar por status")
    public ResponseEntity<List<OrdemProducaoResponse>> listar(
            @RequestParam(required = false) @Parameter(description = "Filtrar por status da produção") StatusProducao status) {
        if (status != null) {
            return ResponseEntity.ok(listarFilaUseCase.listarPorStatus(status));
        }
        return ResponseEntity.ok(consultarUseCase.listarTodas());
    }

    @GetMapping("/fila")
    @Operation(summary = "Obter a fila de produção ativa ordenada por prioridade e tempo de espera")
    public ResponseEntity<List<OrdemProducaoResponse>> obterFilaAtiva() {
        return ResponseEntity.ok(listarFilaUseCase.listarFilaAtiva());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ordem de produção pelo ID")
    public ResponseEntity<OrdemProducaoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(consultarUseCase.buscarPorId(id));
    }

    @GetMapping("/os/{osId}")
    @Operation(summary = "Buscar ordem de produção pelo ID da Ordem de Serviço (OS)")
    public ResponseEntity<OrdemProducaoResponse> buscarPorOsId(@PathVariable Long osId) {
        return ResponseEntity.ok(consultarUseCase.buscarPorOsId(osId));
    }

    @PostMapping("/{id}/diagnostico")
    @Operation(summary = "Atribuir mecânico e iniciar diagnóstico preliminar")
    public ResponseEntity<OrdemProducaoResponse> iniciarDiagnostico(
            @PathVariable String id,
            @Valid @RequestBody IniciarDiagnosticoRequest request) {
        return ResponseEntity.ok(iniciarDiagnosticoUseCase.executar(id, request));
    }

    @PostMapping("/{id}/execucao")
    @Operation(summary = "Iniciar execução dos serviços (notifica OS Service via RabbitMQ)")
    public ResponseEntity<OrdemProducaoResponse> iniciarExecucao(@PathVariable String id) {
        return ResponseEntity.ok(iniciarExecucaoUseCase.executar(id));
    }

    @PostMapping("/{id}/testes")
    @Operation(summary = "Iniciar testes e validação de qualidade veicular")
    public ResponseEntity<OrdemProducaoResponse> iniciarTestes(@PathVariable String id) {
        return ResponseEntity.ok(iniciarTestesUseCase.executar(id));
    }

    @PostMapping("/{id}/apontamentos")
    @Operation(summary = "Adicionar relato técnico / apontamento de oficina")
    public ResponseEntity<OrdemProducaoResponse> adicionarApontamento(
            @PathVariable String id,
            @Valid @RequestBody ApontamentoRequest request) {
        return ResponseEntity.ok(adicionarApontamentoUseCase.executar(id, request));
    }

    @PostMapping("/{id}/finalizar")
    @Operation(summary = "Concluir reparos e liberar veículo (notifica OS Service via RabbitMQ)")
    public ResponseEntity<OrdemProducaoResponse> finalizar(
            @PathVariable String id,
            @RequestBody(required = false) FinalizarProducaoRequest request) {
        return ResponseEntity.ok(finalizarProducaoUseCase.executar(id, request));
    }

    @PostMapping("/{id}/falha")
    @Operation(summary = "Reportar falha técnica ou impossibilidade de reparo (Dispara Saga Rollback)")
    public ResponseEntity<OrdemProducaoResponse> reportarFalha(
            @PathVariable String id,
            @Valid @RequestBody ReportarFalhaRequest request) {
        return ResponseEntity.ok(reportarFalhaUseCase.executar(id, request));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar ordem de produção")
    public ResponseEntity<OrdemProducaoResponse> cancelar(
            @PathVariable String id,
            @RequestParam(defaultValue = "Cancelamento manual solicitado") String motivo) {
        return ResponseEntity.ok(cancelarUseCase.executar(id, motivo));
    }
}
