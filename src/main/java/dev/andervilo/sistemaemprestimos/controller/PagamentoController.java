package dev.andervilo.sistemaemprestimos.controller;

import dev.andervilo.sistemaemprestimos.dto.PagamentoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.PagamentoResponseDTO;
import dev.andervilo.sistemaemprestimos.usecase.pagamento.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final EfetuarPagamentoUseCase efetuarPagamentoUseCase;
    private final BuscarPagamentoPorIdUseCase buscarPagamentoPorIdUseCase;
    private final ListarPagamentosPorEmprestimoUseCase listarPagamentosPorEmprestimoUseCase;

    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> efetuar(@Valid @RequestBody PagamentoRequestDTO request) {
        PagamentoResponseDTO response = efetuarPagamentoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        PagamentoResponseDTO response = buscarPagamentoPorIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/emprestimo/{emprestimoId}")
    public ResponseEntity<List<PagamentoResponseDTO>> listarPorEmprestimo(@PathVariable Long emprestimoId) {
        List<PagamentoResponseDTO> response = listarPagamentosPorEmprestimoUseCase.execute(emprestimoId);
        return ResponseEntity.ok(response);
    }
}
