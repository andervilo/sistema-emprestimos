package dev.andervilo.sistemaemprestimos.dto;

import dev.andervilo.sistemaemprestimos.domain.enums.FormaPagamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoResponseDTO {

    private Long id;
    private Long emprestimoId;
    private LocalDate dataPagamento;
    private BigDecimal valorPago;
    private FormaPagamento formaPagamento;
    private Integer numeroParcela;
    private String observacoes;
    private LocalDateTime dataRegistro;
}
