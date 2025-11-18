package dev.andervilo.sistemaemprestimos.usecase.garantia;

import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.entity.Garantia;
import dev.andervilo.sistemaemprestimos.dto.GarantiaRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.GarantiaResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.GarantiaMapper;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import dev.andervilo.sistemaemprestimos.repository.GarantiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CriarGarantiaUseCase {

    private final GarantiaRepository garantiaRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final GarantiaMapper garantiaMapper;

    @Transactional
    public GarantiaResponseDTO execute(GarantiaRequestDTO request) {
        Emprestimo emprestimo = emprestimoRepository.findById(request.getEmprestimoId())
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));

        Garantia garantia = garantiaMapper.toEntity(request);
        garantia.setEmprestimo(emprestimo);

        Garantia garantiaSalva = garantiaRepository.save(garantia);
        
        return garantiaMapper.toResponseDTO(garantiaSalva);
    }
}
