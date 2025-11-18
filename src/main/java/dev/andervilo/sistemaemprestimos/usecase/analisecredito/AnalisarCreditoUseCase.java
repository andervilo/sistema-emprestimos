package dev.andervilo.sistemaemprestimos.usecase.analisecredito;

import dev.andervilo.sistemaemprestimos.domain.entity.AnaliseCredito;
import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.AnaliseCreditoMapper;
import dev.andervilo.sistemaemprestimos.repository.AnaliseCreditoRepository;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalisarCreditoUseCase {

    private final AnaliseCreditoRepository analiseCreditoRepository;
    private final ClienteRepository clienteRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final AnaliseCreditoMapper analiseCreditoMapper;

    @Transactional
    public AnaliseCreditoResponseDTO execute(AnaliseCreditoRequestDTO request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Emprestimo emprestimo = null;
        if (request.getEmprestimoId() != null) {
            emprestimo = emprestimoRepository.findById(request.getEmprestimoId())
                    .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));
            
            // Atualizar status do empréstimo para EM_ANALISE
            if (emprestimo.getStatus() == StatusEmprestimo.PENDENTE_ANALISE) {
                emprestimo.setStatus(StatusEmprestimo.EM_ANALISE);
                emprestimoRepository.save(emprestimo);
            }
        }

        AnaliseCredito analise = analiseCreditoMapper.toEntity(request);
        analise.setCliente(cliente);
        analise.setEmprestimo(emprestimo);

        AnaliseCredito analiseSalva = analiseCreditoRepository.save(analise);
        
        return analiseCreditoMapper.toResponseDTO(analiseSalva);
    }
}
