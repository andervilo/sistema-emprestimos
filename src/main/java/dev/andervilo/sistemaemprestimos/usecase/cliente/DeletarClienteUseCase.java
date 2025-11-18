package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeletarClienteUseCase {

    private final ClienteRepository clienteRepository;

    @Transactional
    public void execute(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }
        
        clienteRepository.deleteById(id);
    }
}
