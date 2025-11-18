package dev.andervilo.sistemaemprestimos.dto;

import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    private Long id;
    private String nome;
    private String cpfCnpj;
    private TipoPessoa tipoPessoa;
    private String endereco;
    private String cidade;
    private String estado;
    private String cep;
    private String telefone;
    private String email;
    private String historicoCredito;
    private BigDecimal renda;
    private BigDecimal faturamento;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
}
