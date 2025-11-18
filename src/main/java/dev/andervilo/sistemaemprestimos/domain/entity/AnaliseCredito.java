package dev.andervilo.sistemaemprestimos.domain.entity;

import dev.andervilo.sistemaemprestimos.domain.enums.RecomendacaoAnalise;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "analises_credito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnaliseCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprestimo_id")
    private Emprestimo emprestimo;

    @Column(nullable = false)
    private Integer scoreCredito;

    @Column(columnDefinition = "TEXT")
    private String historicoCredito;

    @Column(columnDefinition = "TEXT")
    private String analiseRisco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecomendacaoAnalise recomendacao;

    private String analistaResponsavel;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(updatable = false)
    private LocalDateTime dataAnalise;

    @PrePersist
    protected void onCreate() {
        dataAnalise = LocalDateTime.now();
    }
}
