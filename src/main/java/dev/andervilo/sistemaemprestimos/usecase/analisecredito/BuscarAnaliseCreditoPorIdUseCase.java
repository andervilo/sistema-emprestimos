package dev.andervilo.sistemaemprestimos.usecase.analisecredito;

import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.AnaliseCreditoMapper;
import dev.andervilo.sistemaemprestimos.repository.AnaliseCreditoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarAnaliseCreditoPorIdUseCase {

    private final AnaliseCreditoRepository analiseCreditoRepository;
    private final AnaliseCreditoMapper analiseCreditoMapper;

    @Transactional(readOnly = true)
    public AnaliseCreditoResponseDTO execute(Long id) {
        return analiseCreditoRepository.findById(id)
                .map(analiseCreditoMapper::toResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("Análise de crédito não encontrada"));
    }
}
