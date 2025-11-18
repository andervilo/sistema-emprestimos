package dev.andervilo.sistemaemprestimos.controller;

import dev.andervilo.sistemaemprestimos.dto.EmprestimoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoResponseDTO;
import dev.andervilo.sistemaemprestimos.usecase.emprestimo.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> solicitar(@Valid @RequestBody EmprestimoRequestDTO request) {
        EmprestimoResponseDTO response = solicitarEmprestimoUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> buscarPorId(@PathVariable Long id) {
        EmprestimoResponseDTO response = buscarEmprestimoPorIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EmprestimoResponseDTO>> listar() {
        List<EmprestimoResponseDTO> response = listarEmprestimosUseCase.execute();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<EmprestimoResponseDTO> response = listarEmprestimosPorClienteUseCase.execute(clienteId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<EmprestimoResponseDTO> aprovar(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimentoPrimeiraParcela) {
        EmprestimoResponseDTO response = aprovarEmprestimoUseCase.execute(id, dataVencimentoPrimeiraParcela);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reprovar")
    public ResponseEntity<EmprestimoResponseDTO> reprovar(@PathVariable Long id) {
        EmprestimoResponseDTO response = reprovarEmprestimoUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}
