package dev.andervilo.sistemaemprestimos.usecase.pagamento;

import dev.andervilo.sistemaemprestimos.dto.PagamentoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.PagamentoMapper;
import dev.andervilo.sistemaemprestimos.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarPagamentoPorIdUseCase {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMapper pagamentoMapper;

    @Transactional(readOnly = true)
    public PagamentoResponseDTO execute(Long id) {
        return pagamentoRepository.findById(id)
                .map(pagamentoMapper::toResponseDTO)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado"));
    }
}
