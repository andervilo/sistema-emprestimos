package dev.andervilo.sistemaemprestimos.dto;

import dev.andervilo.sistemaemprestimos.domain.enums.TipoGarantia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarantiaResponseDTO {

    private Long id;
    private Long emprestimoId;
    private TipoGarantia tipoGarantia;
    private String descricao;
    private BigDecimal valorAvaliado;
    private String documentacao;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
}
