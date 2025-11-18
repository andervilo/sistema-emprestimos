package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.dto.ClienteRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.ClienteResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.ClienteMapper;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CriarClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional
    public ClienteResponseDTO execute(ClienteRequestDTO request) {
        if (clienteRepository.existsByCpfCnpj(request.getCpfCnpj())) {
            throw new IllegalArgumentException("CPF/CNPJ já cadastrado");
        }

        Cliente cliente = clienteMapper.toEntity(request);
        Cliente clienteSalvo = clienteRepository.save(cliente);
        
        return clienteMapper.toResponseDTO(clienteSalvo);
    }
}
