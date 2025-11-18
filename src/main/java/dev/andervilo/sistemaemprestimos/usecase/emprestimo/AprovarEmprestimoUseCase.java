package dev.andervilo.sistemaemprestimos.usecase.emprestimo;

import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.EmprestimoMapper;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AprovarEmprestimoUseCase {

    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoMapper emprestimoMapper;

    @Transactional
    public EmprestimoResponseDTO execute(Long emprestimoId, LocalDate dataVencimentoPrimeiraParcela) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));

        if (emprestimo.getStatus() != StatusEmprestimo.PENDENTE_ANALISE && 
            emprestimo.getStatus() != StatusEmprestimo.EM_ANALISE) {
            throw new IllegalStateException("Empréstimo não pode ser aprovado no status atual");
        }

        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setDataAprovacao(LocalDate.now());
        emprestimo.setDataVencimentoPrimeiraParcela(dataVencimentoPrimeiraParcela);

        Emprestimo emprestimoAtualizado = emprestimoRepository.save(emprestimo);
        
        return emprestimoMapper.toResponseDTO(emprestimoAtualizado);
    }
}
