package dev.andervilo.sistemaemprestimos.usecase.emprestimo;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.EmprestimoMapper;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class SolicitarEmprestimoUseCase {

    private final EmprestimoRepository emprestimoRepository;
    private final ClienteRepository clienteRepository;
    private final EmprestimoMapper emprestimoMapper;

    @Transactional
    public EmprestimoResponseDTO execute(EmprestimoRequestDTO request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Emprestimo emprestimo = emprestimoMapper.toEntity(request);
        emprestimo.setCliente(cliente);
        emprestimo.setStatus(StatusEmprestimo.PENDENTE_ANALISE);
        
        // Calcular valor da parcela: PMT = [P * i * (1 + i)^n] / [(1 + i)^n - 1]
        BigDecimal valorParcela = calcularValorParcela(
                request.getValorEmprestimo(),
                request.getTaxaJuros(),
                request.getNumeroParcelas()
        );
        emprestimo.setValorParcela(valorParcela);

        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);
        
        return emprestimoMapper.toResponseDTO(emprestimoSalvo);
    }

    private BigDecimal calcularValorParcela(BigDecimal valor, BigDecimal taxa, Integer parcelas) {
        if (taxa.compareTo(BigDecimal.ZERO) == 0) {
            return valor.divide(BigDecimal.valueOf(parcelas), 2, RoundingMode.HALF_UP);
        }

        BigDecimal taxaMensal = taxa.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        BigDecimal umMaisTaxa = BigDecimal.ONE.add(taxaMensal);
        BigDecimal potencia = umMaisTaxa.pow(parcelas);
        
        BigDecimal numerador = valor.multiply(taxaMensal).multiply(potencia);
        BigDecimal denominador = potencia.subtract(BigDecimal.ONE);
        
        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }
}
