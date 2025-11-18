package dev.andervilo.sistemaemprestimos.controller;

import dev.andervilo.sistemaemprestimos.dto.GarantiaRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.GarantiaResponseDTO;
import dev.andervilo.sistemaemprestimos.usecase.garantia.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/garantias")
@RequiredArgsConstructor
public class GarantiaController {

    private final CriarGarantiaUseCase criarGarantiaUseCase;
    private final BuscarGarantiaPorIdUseCase buscarGarantiaPorIdUseCase;
    private final ListarGarantiasPorEmprestimoUseCase listarGarantiasPorEmprestimoUseCase;
    private final AtualizarGarantiaUseCase atualizarGarantiaUseCase;
    private final DeletarGarantiaUseCase deletarGarantiaUseCase;

    @PostMapping
    public ResponseEntity<GarantiaResponseDTO> criar(@Valid @RequestBody GarantiaRequestDTO request) {
        GarantiaResponseDTO response = criarGarantiaUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GarantiaResponseDTO> buscarPorId(@PathVariable Long id) {
        GarantiaResponseDTO response = buscarGarantiaPorIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/emprestimo/{emprestimoId}")
    public ResponseEntity<List<GarantiaResponseDTO>> listarPorEmprestimo(@PathVariable Long emprestimoId) {
        List<GarantiaResponseDTO> response = listarGarantiasPorEmprestimoUseCase.execute(emprestimoId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GarantiaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody GarantiaRequestDTO request) {
        GarantiaResponseDTO response = atualizarGarantiaUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarGarantiaUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
