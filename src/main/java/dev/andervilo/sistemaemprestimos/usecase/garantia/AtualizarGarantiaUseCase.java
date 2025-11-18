package dev.andervilo.sistemaemprestimos.usecase.garantia;

import dev.andervilo.sistemaemprestimos.domain.entity.Garantia;
import dev.andervilo.sistemaemprestimos.dto.GarantiaRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.GarantiaResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.GarantiaMapper;
import dev.andervilo.sistemaemprestimos.repository.GarantiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtualizarGarantiaUseCase {

    private final GarantiaRepository garantiaRepository;
    private final GarantiaMapper garantiaMapper;

    @Transactional
    public GarantiaResponseDTO execute(Long id, GarantiaRequestDTO request) {
        Garantia garantia = garantiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Garantia não encontrada"));

        garantiaMapper.updateEntityFromDTO(request, garantia);
        Garantia garantiaAtualizada = garantiaRepository.save(garantia);
        
        return garantiaMapper.toResponseDTO(garantiaAtualizada);
    }
}
