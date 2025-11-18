package dev.andervilo.sistemaemprestimos.usecase.pagamento;

import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.entity.Pagamento;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.dto.PagamentoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.PagamentoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.PagamentoMapper;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import dev.andervilo.sistemaemprestimos.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EfetuarPagamentoUseCase {

    private final PagamentoRepository pagamentoRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final PagamentoMapper pagamentoMapper;

    @Transactional
    public PagamentoResponseDTO execute(PagamentoRequestDTO request) {
        Emprestimo emprestimo = emprestimoRepository.findById(request.getEmprestimoId())
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado"));

        if (emprestimo.getStatus() != StatusEmprestimo.ATIVO && 
            emprestimo.getStatus() != StatusEmprestimo.ATRASADO) {
            throw new IllegalStateException("Empréstimo não está em situação que permite pagamentos");
        }

        Pagamento pagamento = pagamentoMapper.toEntity(request);
        pagamento.setEmprestimo(emprestimo);

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);

        // Verificar se o empréstimo foi totalmente pago
        verificarEmprestimoPago(emprestimo);

        return pagamentoMapper.toResponseDTO(pagamentoSalvo);
    }

    private void verificarEmprestimoPago(Emprestimo emprestimo) {
        BigDecimal totalPago = pagamentoRepository.sumValorPagoByEmprestimoId(emprestimo.getId());
        BigDecimal totalEmprestimo = emprestimo.getValorParcela()
                .multiply(BigDecimal.valueOf(emprestimo.getNumeroParcelas()));

        if (totalPago != null && totalPago.compareTo(totalEmprestimo) >= 0) {
            emprestimo.setStatus(StatusEmprestimo.PAGO);
            emprestimoRepository.save(emprestimo);
        }
    }
}
