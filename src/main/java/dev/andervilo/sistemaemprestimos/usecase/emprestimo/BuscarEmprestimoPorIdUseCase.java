package dev.andervilo.sistemaemprestimos.usecase.emprestimo;

import dev.andervilo.sistemaemprestimos.dto.EmprestimoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.EmprestimoMapper;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarEmprestimoPorIdUseCase {

    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoMapper emprestimoMapper;

    @Transactional(readOnly = true)
    public EmprestimoResponseDTO execute(Long id) {
        return emprestimoRepository.findById(id)
                .map(emprestimoMapper::toResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));
    }
}
