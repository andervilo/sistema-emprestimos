package dev.andervilo.sistemaemprestimos.usecase.garantia;

import dev.andervilo.sistemaemprestimos.dto.GarantiaResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.GarantiaMapper;
import dev.andervilo.sistemaemprestimos.repository.GarantiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarGarantiaPorIdUseCase {

    private final GarantiaRepository garantiaRepository;
    private final GarantiaMapper garantiaMapper;

    @Transactional(readOnly = true)
    public GarantiaResponseDTO execute(Long id) {
        return garantiaRepository.findById(id)
                .map(garantiaMapper::toResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("Garantia não encontrada"));
    }
}
