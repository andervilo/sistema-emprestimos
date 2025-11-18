package dev.andervilo.sistemaemprestimos.usecase.garantia;

import dev.andervilo.sistemaemprestimos.repository.GarantiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeletarGarantiaUseCase {

    private final GarantiaRepository garantiaRepository;

    @Transactional
    public void execute(Long id) {
        if (!garantiaRepository.existsById(id)) {
            throw new IllegalArgumentException("Garantia não encontrada");
        }
        
        garantiaRepository.deleteById(id);
    }
}
