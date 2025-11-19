package dev.andervilo.sistemaemprestimos.controller;

import dev.andervilo.sistemaemprestimos.dto.GarantiaRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.GarantiaResponseDTO;
import dev.andervilo.sistemaemprestimos.usecase.garantia.*;
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

@Tag(name = "Garantias", description = "Gerenciamento de garantias associadas a empréstimos")
@RestController
@RequestMapping("/api/garantias")
@RequiredArgsConstructor
public class GarantiaController {

    private final CriarGarantiaUseCase criarGarantiaUseCase;
    private final BuscarGarantiaPorIdUseCase buscarGarantiaPorIdUseCase;
    private final ListarGarantiasPorEmprestimoUseCase listarGarantiasPorEmprestimoUseCase;
    private final AtualizarGarantiaUseCase atualizarGarantiaUseCase;
    private final DeletarGarantiaUseCase deletarGarantiaUseCase;

    @Operation(summary = "Criar garantia", description = "Registra uma nova garantia para um empréstimo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Garantia criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    @PostMapping
    public ResponseEntity<GarantiaResponseDTO> criar(@Valid @RequestBody GarantiaRequestDTO request) {
        GarantiaResponseDTO response = criarGarantiaUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar garantia por ID", description = "Retorna os dados de uma garantia específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Garantia encontrada"),
        @ApiResponse(responseCode = "404", description = "Garantia não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GarantiaResponseDTO> buscarPorId(@PathVariable Long id) {
        GarantiaResponseDTO response = buscarGarantiaPorIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar garantias por empréstimo", description = "Retorna todas as garantias de um empréstimo")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/emprestimo/{emprestimoId}")
    public ResponseEntity<List<GarantiaResponseDTO>> listarPorEmprestimo(@PathVariable Long emprestimoId) {
        List<GarantiaResponseDTO> response = listarGarantiasPorEmprestimoUseCase.execute(emprestimoId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar garantia", description = "Atualiza os dados de uma garantia existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Garantia atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Garantia não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GarantiaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody GarantiaRequestDTO request) {
        GarantiaResponseDTO response = atualizarGarantiaUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deletar garantia", description = "Remove uma garantia do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Garantia deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Garantia não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarGarantiaUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
