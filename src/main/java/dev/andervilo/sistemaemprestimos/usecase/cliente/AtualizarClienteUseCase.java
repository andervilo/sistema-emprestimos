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
public class AtualizarClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional
    public ClienteResponseDTO execute(Long id, ClienteRequestDTO request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        if (!cliente.getCpfCnpj().equals(request.getCpfCnpj()) && 
            clienteRepository.existsByCpfCnpj(request.getCpfCnpj())) {
            throw new IllegalArgumentException("CPF/CNPJ já cadastrado para outro cliente");
        }

        clienteMapper.updateEntityFromDTO(request, cliente);
        Cliente clienteAtualizado = clienteRepository.save(cliente);
        
        return clienteMapper.toResponseDTO(clienteAtualizado);
    }
}
