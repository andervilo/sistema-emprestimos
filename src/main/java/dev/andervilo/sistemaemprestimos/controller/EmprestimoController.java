package dev.andervilo.sistemaemprestimos.controller;

import dev.andervilo.sistemaemprestimos.dto.EmprestimoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoResponseDTO;
import dev.andervilo.sistemaemprestimos.usecase.emprestimo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Empréstimos", description = "Gerenciamento de solicitações e contratos de empréstimo")
@RestController
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final SolicitarEmprestimoUseCase solicitarEmprestimoUseCase;
    private final BuscarEmprestimoPorIdUseCase buscarEmprestimoPorIdUseCase;
    private final ListarEmprestimosUseCase listarEmprestimosUseCase;
    private final ListarEmprestimosPorClienteUseCase listarEmprestimosPorClienteUseCase;
    private final AprovarEmprestimoUseCase aprovarEmprestimoUseCase;
    private final ReprovarEmprestimoUseCase reprovarEmprestimoUseCase;

    @Operation(summary = "Solicitar empréstimo", description = "Cria uma nova solicitação de empréstimo com cálculo automático das parcelas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Solicitação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> solicitar(@Valid @RequestBody EmprestimoRequestDTO request) {
        EmprestimoResponseDTO response = solicitarEmprestimoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar empréstimo por ID", description = "Retorna os dados de um empréstimo específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empréstimo encontrado"),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> buscarPorId(@PathVariable Long id) {
        EmprestimoResponseDTO response = buscarEmprestimoPorIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todos os empréstimos", description = "Retorna a lista completa de empréstimos")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<EmprestimoResponseDTO>> listar() {
        List<EmprestimoResponseDTO> response = listarEmprestimosUseCase.execute();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar empréstimos por cliente", description = "Retorna todos os empréstimos de um cliente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<EmprestimoResponseDTO> response = listarEmprestimosPorClienteUseCase.execute(clienteId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Aprovar empréstimo", description = "Aprova uma solicitação de empréstimo e ativa o contrato")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empréstimo aprovado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado"),
        @ApiResponse(responseCode = "400", description = "Status inválido para aprovação")
    })
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<EmprestimoResponseDTO> aprovar(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimentoPrimeiraParcela) {
        EmprestimoResponseDTO response = aprovarEmprestimoUseCase.execute(id, dataVencimentoPrimeiraParcela);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reprovar empréstimo", description = "Reprova uma solicitação de empréstimo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empréstimo reprovado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado"),
        @ApiResponse(responseCode = "400", description = "Status inválido para reprovação")
    })
    @PatchMapping("/{id}/reprovar")
    public ResponseEntity<EmprestimoResponseDTO> reprovar(@PathVariable Long id) {
        EmprestimoResponseDTO response = reprovarEmprestimoUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}
