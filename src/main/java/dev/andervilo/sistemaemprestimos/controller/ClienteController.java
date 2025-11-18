package dev.andervilo.sistemaemprestimos.controller;

import dev.andervilo.sistemaemprestimos.dto.ClienteRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.ClienteResponseDTO;
import dev.andervilo.sistemaemprestimos.usecase.cliente.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final CriarClienteUseCase criarClienteUseCase;
    private final BuscarClientePorIdUseCase buscarClientePorIdUseCase;
    private final BuscarClientePorCpfCnpjUseCase buscarClientePorCpfCnpjUseCase;
    private final ListarClientesUseCase listarClientesUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final DeletarClienteUseCase deletarClienteUseCase;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criar(@Valid @RequestBody ClienteRequestDTO request) {
        ClienteResponseDTO response = criarClienteUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        ClienteResponseDTO response = buscarClientePorIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cpf-cnpj/{cpfCnpj}")
    public ResponseEntity<ClienteResponseDTO> buscarPorCpfCnpj(@PathVariable String cpfCnpj) {
        ClienteResponseDTO response = buscarClientePorCpfCnpjUseCase.execute(cpfCnpj);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listar() {
        List<ClienteResponseDTO> response = listarClientesUseCase.execute();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO request) {
        ClienteResponseDTO response = atualizarClienteUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarClienteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
