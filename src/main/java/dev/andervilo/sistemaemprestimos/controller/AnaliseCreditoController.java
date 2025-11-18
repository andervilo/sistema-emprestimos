package dev.andervilo.sistemaemprestimos.controller;

import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoResponseDTO;
import dev.andervilo.sistemaemprestimos.usecase.analisecredito.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analises-credito")
@RequiredArgsConstructor
public class AnaliseCreditoController {

    private final AnalisarCreditoUseCase analisarCreditoUseCase;
    private final BuscarAnaliseCreditoPorIdUseCase buscarAnaliseCreditoPorIdUseCase;
    private final BuscarAnaliseCreditoPorEmprestimoUseCase buscarAnaliseCreditoPorEmprestimoUseCase;
    private final ListarAnalisesCreditoPorClienteUseCase listarAnalisesCreditoPorClienteUseCase;

    @PostMapping
    public ResponseEntity<AnaliseCreditoResponseDTO> analisar(@Valid @RequestBody AnaliseCreditoRequestDTO request) {
        AnaliseCreditoResponseDTO response = analisarCreditoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnaliseCreditoResponseDTO> buscarPorId(@PathVariable Long id) {
        AnaliseCreditoResponseDTO response = buscarAnaliseCreditoPorIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/emprestimo/{emprestimoId}")
    public ResponseEntity<AnaliseCreditoResponseDTO> buscarPorEmprestimo(@PathVariable Long emprestimoId) {
        AnaliseCreditoResponseDTO response = buscarAnaliseCreditoPorEmprestimoUseCase.execute(emprestimoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<AnaliseCreditoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<AnaliseCreditoResponseDTO> response = listarAnalisesCreditoPorClienteUseCase.execute(clienteId);
        return ResponseEntity.ok(response);
    }
}
