package dev.andervilo.sistemaemprestimos.usecase.garantia;

import dev.andervilo.sistemaemprestimos.dto.GarantiaResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.GarantiaMapper;
import dev.andervilo.sistemaemprestimos.repository.GarantiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarGarantiasPorEmprestimoUseCase {

    private final GarantiaRepository garantiaRepository;
    private final GarantiaMapper garantiaMapper;

    @Transactional(readOnly = true)
    public List<GarantiaResponseDTO> execute(Long emprestimoId) {
        return garantiaRepository.findByEmprestimoId(emprestimoId)
                .stream()
                .map(garantiaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
