package dev.andervilo.sistemaemprestimos.usecase.analisecredito;

import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.AnaliseCreditoMapper;
import dev.andervilo.sistemaemprestimos.repository.AnaliseCreditoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarAnalisesCreditoPorClienteUseCase {

    private final AnaliseCreditoRepository analiseCreditoRepository;
    private final AnaliseCreditoMapper analiseCreditoMapper;

    @Transactional(readOnly = true)
    public List<AnaliseCreditoResponseDTO> execute(Long clienteId) {
        return analiseCreditoRepository.findByClienteIdOrderByDataAnaliseDesc(clienteId)
                .stream()
                .map(analiseCreditoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
