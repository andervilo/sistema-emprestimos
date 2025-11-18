package dev.andervilo.sistemaemprestimos.dto;

import dev.andervilo.sistemaemprestimos.domain.enums.TipoGarantia;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarantiaRequestDTO {

    @NotNull(message = "ID do empréstimo é obrigatório")
    private Long emprestimoId;

    @NotNull(message = "Tipo de garantia é obrigatório")
    private TipoGarantia tipoGarantia;

    private String descricao;

    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valorAvaliado;

    private String documentacao;
}
