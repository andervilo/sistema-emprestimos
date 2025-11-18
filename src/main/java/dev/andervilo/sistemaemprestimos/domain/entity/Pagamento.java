package dev.andervilo.sistemaemprestimos.domain.entity;

import dev.andervilo.sistemaemprestimos.domain.enums.FormaPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprestimo_id", nullable = false)
    private Emprestimo emprestimo;

    @Column(nullable = false)
    private LocalDate dataPagamento;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPagamento formaPagamento;

    private Integer numeroParcela;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(updatable = false)
    private LocalDateTime dataRegistro;

    @PrePersist
    protected void onCreate() {
        dataRegistro = LocalDateTime.now();
    }
}
