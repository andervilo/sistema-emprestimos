package dev.andervilo.sistemaemprestimos.dto;

import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoResponseDTO {

    private Long id;
    private Long clienteId;
    private String clienteNome;
    private BigDecimal valorEmprestimo;
    private BigDecimal taxaJuros;
    private Integer numeroParcelas;
    private BigDecimal valorParcela;
    private LocalDate dataAprovacao;
    private LocalDate dataVencimentoPrimeiraParcela;
    private StatusEmprestimo status;
    private String finalidade;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataAtualizacao;
}
