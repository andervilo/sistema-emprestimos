package dev.andervilo.sistemaemprestimos.dto;

import dev.andervilo.sistemaemprestimos.domain.enums.RecomendacaoAnalise;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnaliseCreditoRequestDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    private Long emprestimoId;

    @NotNull(message = "Score de crédito é obrigatório")
    @Min(value = 0, message = "Score deve ser no mínimo 0")
    @Max(value = 1000, message = "Score deve ser no máximo 1000")
    private Integer scoreCredito;

    private String historicoCredito;

    private String analiseRisco;

    @NotNull(message = "Recomendação é obrigatória")
    private RecomendacaoAnalise recomendacao;

    private String analistaResponsavel;

    private String observacoes;
}
