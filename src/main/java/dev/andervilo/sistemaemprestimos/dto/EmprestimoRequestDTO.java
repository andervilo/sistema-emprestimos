package dev.andervilo.sistemaemprestimos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoRequestDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "Valor do empréstimo é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valorEmprestimo;

    @NotNull(message = "Taxa de juros é obrigatória")
    @DecimalMin(value = "0.0", message = "Taxa de juros deve ser maior ou igual a zero")
    private BigDecimal taxaJuros;

    @NotNull(message = "Número de parcelas é obrigatório")
    @Min(value = 1, message = "Número de parcelas deve ser no mínimo 1")
    private Integer numeroParcelas;

    private String finalidade;
}
