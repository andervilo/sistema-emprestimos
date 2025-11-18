package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.dto.ClienteResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.ClienteMapper;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarClientePorIdUseCase {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional(readOnly = true)
    public ClienteResponseDTO execute(Long id) {
        return clienteRepository.findById(id)
                .map(clienteMapper::toResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }
}
