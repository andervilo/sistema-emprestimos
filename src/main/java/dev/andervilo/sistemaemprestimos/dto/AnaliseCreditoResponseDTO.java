package dev.andervilo.sistemaemprestimos.dto;

import dev.andervilo.sistemaemprestimos.domain.enums.RecomendacaoAnalise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnaliseCreditoResponseDTO {

    private Long id;
    private Long clienteId;
    private String clienteNome;
    private Long emprestimoId;
    private Integer scoreCredito;
    private String historicoCredito;
    private String analiseRisco;
    private RecomendacaoAnalise recomendacao;
    private String analistaResponsavel;
    private String observacoes;
    private LocalDateTime dataAnalise;
}
