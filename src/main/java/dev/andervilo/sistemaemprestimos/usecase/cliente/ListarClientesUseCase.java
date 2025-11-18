package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.dto.ClienteResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.ClienteMapper;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarClientesUseCase {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> execute() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
