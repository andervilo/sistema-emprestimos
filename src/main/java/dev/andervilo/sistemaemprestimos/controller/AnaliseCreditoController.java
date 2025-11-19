package dev.andervilo.sistemaemprestimos.controller;

import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoResponseDTO;
import dev.andervilo.sistemaemprestimos.usecase.analisecredito.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Análise de Crédito", description = "Gerenciamento de análises de crédito e avaliação de risco")
@RestController
@RequestMapping("/api/analises-credito")
@RequiredArgsConstructor
public class AnaliseCreditoController {

    private final AnalisarCreditoUseCase analisarCreditoUseCase;
    private final BuscarAnaliseCreditoPorIdUseCase buscarAnaliseCreditoPorIdUseCase;
    private final BuscarAnaliseCreditoPorEmprestimoUseCase buscarAnaliseCreditoPorEmprestimoUseCase;
    private final ListarAnalisesCreditoPorClienteUseCase listarAnalisesCreditoPorClienteUseCase;

    @Operation(summary = "Criar análise de crédito", description = "Registra uma nova análise de crédito para um cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Análise criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente ou empréstimo não encontrado")
    })
    @PostMapping
    public ResponseEntity<AnaliseCreditoResponseDTO> analisar(@Valid @RequestBody AnaliseCreditoRequestDTO request) {
        AnaliseCreditoResponseDTO response = analisarCreditoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar análise por ID", description = "Retorna uma análise de crédito específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Análise encontrada"),
        @ApiResponse(responseCode = "404", description = "Análise não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AnaliseCreditoResponseDTO> buscarPorId(@PathVariable Long id) {
        AnaliseCreditoResponseDTO response = buscarAnaliseCreditoPorIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar análise por empréstimo", description = "Retorna a análise de crédito de um empréstimo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Análise encontrada"),
        @ApiResponse(responseCode = "404", description = "Análise não encontrada")
    })
    @GetMapping("/emprestimo/{emprestimoId}")
    public ResponseEntity<AnaliseCreditoResponseDTO> buscarPorEmprestimo(@PathVariable Long emprestimoId) {
        AnaliseCreditoResponseDTO response = buscarAnaliseCreditoPorEmprestimoUseCase.execute(emprestimoId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar análises por cliente", description = "Retorna todas as análises de crédito de um cliente")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<AnaliseCreditoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<AnaliseCreditoResponseDTO> response = listarAnalisesCreditoPorClienteUseCase.execute(clienteId);
        return ResponseEntity.ok(response);
    }
}
