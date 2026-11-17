package com.oficina.production.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oficina.production.application.dto.*;
import com.oficina.production.application.usecase.*;
import com.oficina.production.domain.model.PrioridadeProducao;
import com.oficina.production.domain.model.StatusProducao;
import com.oficina.production.infrastructure.api.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Controller: OrdemProducaoController")
class OrdemProducaoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnfileirarOrdemProducaoUseCase enfileirarUseCase;
    @Mock
    private IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    @Mock
    private IniciarExecucaoUseCase iniciarExecucaoUseCase;
    @Mock
    private IniciarTestesUseCase iniciarTestesUseCase;
    @Mock
    private AdicionarApontamentoUseCase adicionarApontamentoUseCase;
    @Mock
    private FinalizarProducaoUseCase finalizarProducaoUseCase;
    @Mock
    private ReportarFalhaProducaoUseCase reportarFalhaUseCase;
    @Mock
    private CancelarOrdemProducaoUseCase cancelarUseCase;
    @Mock
    private ConsultarOrdemProducaoUseCase consultarUseCase;
    @Mock
    private ListarFilaProducaoUseCase listarFilaUseCase;

    @InjectMocks
    private OrdemProducaoController controller;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private OrdemProducaoResponse responsePadrao;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        responsePadrao = OrdemProducaoResponse.builder()
                .id("p-1")
                .osId(1L)
                .numeroOs("OS-1")
                .veiculoPlaca("BRA2E19")
                .veiculoModelo("Corolla")
                .status(StatusProducao.NA_FILA)
                .prioridade(PrioridadeProducao.NORMAL)
                .dataEntradaFila(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/producao/ordens - Deve enfileirar ordem com sucesso")
    void deveEnfileirar() throws Exception {
        EnfileirarOrdemRequest request = EnfileirarOrdemRequest.builder()
                .osId(1L)
                .numeroOs("OS-1")
                .veiculoPlaca("BRA2E19")
                .veiculoModelo("Corolla")
                .descricaoServicos("Revisão")
                .prioridade(PrioridadeProducao.NORMAL)
                .build();

        when(enfileirarUseCase.executar(any())).thenReturn(responsePadrao);

        mockMvc.perform(post("/api/v1/producao/ordens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("p-1"))
                .andExpect(jsonPath("$.numeroOs").value("OS-1"));
    }

    @Test
    @DisplayName("GET /api/v1/producao/ordens/fila - Deve listar fila ativa")
    void deveListarFila() throws Exception {
        when(listarFilaUseCase.listarFilaAtiva()).thenReturn(List.of(responsePadrao));

        mockMvc.perform(get("/api/v1/producao/ordens/fila"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("p-1"));
    }

    @Test
    @DisplayName("GET /api/v1/producao/ordens/{id} - Deve buscar por ID")
    void deveBuscarPorId() throws Exception {
        when(consultarUseCase.buscarPorId("p-1")).thenReturn(responsePadrao);

        mockMvc.perform(get("/api/v1/producao/ordens/p-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p-1"));
    }

    @Test
    @DisplayName("POST /api/v1/producao/ordens/{id}/diagnostico - Deve iniciar diagnóstico")
    void deveIniciarDiagnostico() throws Exception {
        IniciarDiagnosticoRequest req = IniciarDiagnosticoRequest.builder()
                .matriculaMecanico("MEC-1")
                .nomeMecanico("Silvio")
                .build();

        responsePadrao.setStatus(StatusProducao.EM_DIAGNOSTICO);
        when(iniciarDiagnosticoUseCase.executar(eq("p-1"), any())).thenReturn(responsePadrao);

        mockMvc.perform(post("/api/v1/producao/ordens/p-1/diagnostico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_DIAGNOSTICO"));
    }

    @Test
    @DisplayName("POST /api/v1/producao/ordens/{id}/execucao - Deve iniciar execução")
    void deveIniciarExecucao() throws Exception {
        responsePadrao.setStatus(StatusProducao.EM_EXECUCAO);
        when(iniciarExecucaoUseCase.executar("p-1")).thenReturn(responsePadrao);

        mockMvc.perform(post("/api/v1/producao/ordens/p-1/execucao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_EXECUCAO"));
    }

    @Test
    @DisplayName("POST /api/v1/producao/ordens/{id}/testes - Deve iniciar testes")
    void deveIniciarTestes() throws Exception {
        responsePadrao.setStatus(StatusProducao.EM_TESTES);
        when(iniciarTestesUseCase.executar("p-1")).thenReturn(responsePadrao);

        mockMvc.perform(post("/api/v1/producao/ordens/p-1/testes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_TESTES"));
    }

    @Test
    @DisplayName("POST /api/v1/producao/ordens/{id}/finalizar - Deve finalizar produção")
    void deveFinalizar() throws Exception {
        responsePadrao.setStatus(StatusProducao.FINALIZADO);
        when(finalizarProducaoUseCase.executar(eq("p-1"), any())).thenReturn(responsePadrao);

        mockMvc.perform(post("/api/v1/producao/ordens/p-1/finalizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FinalizarProducaoRequest("Tudo pronto"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZADO"));
    }

    @Test
    @DisplayName("POST /api/v1/producao/ordens/{id}/falha - Deve reportar falha")
    void deveReportarFalha() throws Exception {
        responsePadrao.setStatus(StatusProducao.FALHA_TECNICA);
        when(reportarFalhaUseCase.executar(eq("p-1"), any())).thenReturn(responsePadrao);

        mockMvc.perform(post("/api/v1/producao/ordens/p-1/falha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReportarFalhaRequest("Peça trincada"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FALHA_TECNICA"));
    }

    @Test
    @DisplayName("GET /api/v1/producao/ordens - Deve listar todas ou filtrar por status")
    void deveListarTodasOuPorStatus() throws Exception {
        when(consultarUseCase.listarTodas()).thenReturn(List.of(responsePadrao));
        mockMvc.perform(get("/api/v1/producao/ordens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("p-1"));

        when(listarFilaUseCase.listarPorStatus(StatusProducao.NA_FILA)).thenReturn(List.of(responsePadrao));
        mockMvc.perform(get("/api/v1/producao/ordens").param("status", "NA_FILA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("NA_FILA"));
    }

    @Test
    @DisplayName("GET /api/v1/producao/ordens/os/{osId} - Deve buscar por OS ID")
    void deveBuscarPorOsId() throws Exception {
        when(consultarUseCase.buscarPorOsId(1L)).thenReturn(responsePadrao);
        mockMvc.perform(get("/api/v1/producao/ordens/os/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.osId").value(1));
    }

    @Test
    @DisplayName("POST /api/v1/producao/ordens/{id}/apontamentos - Deve adicionar apontamento")
    void deveAdicionarApontamento() throws Exception {
        ApontamentoRequest req = ApontamentoRequest.builder().autor("Carlos").descricao("Serviço feito").build();
        when(adicionarApontamentoUseCase.executar(eq("p-1"), any())).thenReturn(responsePadrao);

        mockMvc.perform(post("/api/v1/producao/ordens/p-1/apontamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/producao/ordens/{id}/cancelar - Deve cancelar")
    void deveCancelar() throws Exception {
        responsePadrao.setStatus(StatusProducao.CANCELADO);
        when(cancelarUseCase.executar(eq("p-1"), any())).thenReturn(responsePadrao);

        mockMvc.perform(post("/api/v1/producao/ordens/p-1/cancelar").param("motivo", "Cancelado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));
    }
}
