package dev.andervilo.sistemaemprestimos.domain.entity;

import dev.andervilo.sistemaemprestimos.domain.enums.TipoGarantia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "garantias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Garantia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprestimo_id", nullable = false)
    private Emprestimo emprestimo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoGarantia tipoGarantia;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(precision = 15, scale = 2)
    private BigDecimal valorAvaliado;

    @Column(columnDefinition = "TEXT")
    private String documentacao;

    @Column(updatable = false)
    private LocalDateTime dataCadastro;

    private LocalDateTime dataAtualizacao;

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
