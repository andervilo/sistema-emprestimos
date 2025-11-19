package dev.andervilo.sistemaemprestimos.controller;

import dev.andervilo.sistemaemprestimos.dto.PagamentoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.PagamentoResponseDTO;
import dev.andervilo.sistemaemprestimos.usecase.pagamento.*;
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

@Tag(name = "Pagamentos", description = "Gerenciamento de pagamentos de parcelas de empréstimos")
@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final EfetuarPagamentoUseCase efetuarPagamentoUseCase;
    private final BuscarPagamentoPorIdUseCase buscarPagamentoPorIdUseCase;
    private final ListarPagamentosPorEmprestimoUseCase listarPagamentosPorEmprestimoUseCase;

    @Operation(summary = "Efetuar pagamento", description = "Registra um novo pagamento de parcela de empréstimo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pagamento registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou empréstimo em situação inadequada"),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> efetuar(@Valid @RequestBody PagamentoRequestDTO request) {
        PagamentoResponseDTO response = efetuarPagamentoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar pagamento por ID", description = "Retorna os dados de um pagamento específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        PagamentoResponseDTO response = buscarPagamentoPorIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar pagamentos por empréstimo", description = "Retorna todos os pagamentos de um empréstimo")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/emprestimo/{emprestimoId}")
    public ResponseEntity<List<PagamentoResponseDTO>> listarPorEmprestimo(@PathVariable Long emprestimoId) {
        List<PagamentoResponseDTO> response = listarPagamentosPorEmprestimoUseCase.execute(emprestimoId);
        return ResponseEntity.ok(response);
    }
}
