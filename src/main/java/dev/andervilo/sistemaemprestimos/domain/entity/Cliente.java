package dev.andervilo.sistemaemprestimos.domain.entity;

import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpfCnpj;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPessoa tipoPessoa;

    private String endereco;

    private String cidade;

    private String estado;

    private String cep;

    private String telefone;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String historicoCredito;

    // Específico para Pessoa Física
    private BigDecimal renda;

    // Específico para Pessoa Jurídica
    private BigDecimal faturamento;

    @Column(updatable = false)
    private LocalDateTime dataCadastro;

    private LocalDateTime dataAtualizacao;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Emprestimo> emprestimos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnaliseCredito> analises = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
