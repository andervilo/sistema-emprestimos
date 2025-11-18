package dev.andervilo.sistemaemprestimos.domain.entity;

import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emprestimos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorEmprestimo;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxaJuros;

    @Column(nullable = false)
    private Integer numeroParcelas;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorParcela;

    private LocalDate dataAprovacao;

    private LocalDate dataVencimentoPrimeiraParcela;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEmprestimo status = StatusEmprestimo.PENDENTE_ANALISE;

    @Column(columnDefinition = "TEXT")
    private String finalidade;

    @Column(updatable = false)
    private LocalDateTime dataSolicitacao;

    private LocalDateTime dataAtualizacao;

    @OneToMany(mappedBy = "emprestimo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamentos = new ArrayList<>();

    @OneToMany(mappedBy = "emprestimo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Garantia> garantias = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataSolicitacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
