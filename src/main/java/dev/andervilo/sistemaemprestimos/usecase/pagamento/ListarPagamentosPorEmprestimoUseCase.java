package dev.andervilo.sistemaemprestimos.usecase.pagamento;

import dev.andervilo.sistemaemprestimos.dto.PagamentoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.PagamentoMapper;
import dev.andervilo.sistemaemprestimos.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarPagamentosPorEmprestimoUseCase {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMapper pagamentoMapper;

    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> execute(Long emprestimoId) {
        return pagamentoRepository.findByEmprestimoIdOrderByDataPagamentoDesc(emprestimoId)
                .stream()
                .map(pagamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
