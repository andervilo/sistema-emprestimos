package dev.andervilo.sistemaemprestimos.repository;

import dev.andervilo.sistemaemprestimos.domain.entity.AnaliseCredito;
import dev.andervilo.sistemaemprestimos.domain.enums.RecomendacaoAnalise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnaliseCreditoRepository extends JpaRepository<AnaliseCredito, Long> {

    List<AnaliseCredito> findByClienteId(Long clienteId);

    Optional<AnaliseCredito> findByEmprestimoId(Long emprestimoId);

    List<AnaliseCredito> findByRecomendacao(RecomendacaoAnalise recomendacao);

    @Query("SELECT a FROM AnaliseCredito a WHERE a.cliente.id = :clienteId ORDER BY a.dataAnalise DESC")
    List<AnaliseCredito> findByClienteIdOrderByDataAnaliseDesc(Long clienteId);

    @Query("SELECT a FROM AnaliseCredito a WHERE a.scoreCredito >= :scoreMinimo")
    List<AnaliseCredito> findByScoreCreditoGreaterThanEqual(Integer scoreMinimo);

    @Query("SELECT a FROM AnaliseCredito a WHERE a.dataAnalise BETWEEN :dataInicio AND :dataFim")
    List<AnaliseCredito> findByDataAnaliseBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("SELECT a FROM AnaliseCredito a WHERE a.analistaResponsavel = :analista")
    List<AnaliseCredito> findByAnalistaResponsavel(String analista);

    @Query("SELECT AVG(a.scoreCredito) FROM AnaliseCredito a WHERE a.cliente.id = :clienteId")
    Double getAverageScoreByClienteId(Long clienteId);

    @Query("SELECT a FROM AnaliseCredito a LEFT JOIN FETCH a.emprestimo WHERE a.id = :id")
    Optional<AnaliseCredito> findByIdWithEmprestimo(Long id);
}
