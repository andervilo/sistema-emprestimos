package dev.andervilo.sistemaemprestimos.repository;

import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    List<Emprestimo> findByClienteId(Long clienteId);

    List<Emprestimo> findByStatus(StatusEmprestimo status);

    @Query("SELECT e FROM Emprestimo e WHERE e.cliente.id = :clienteId AND e.status = :status")
    List<Emprestimo> findByClienteIdAndStatus(Long clienteId, StatusEmprestimo status);

    @Query("SELECT e FROM Emprestimo e WHERE e.status = :status AND e.dataVencimentoPrimeiraParcela < :data")
    List<Emprestimo> findByStatusAndDataVencimentoBefore(StatusEmprestimo status, LocalDate data);

    @Query("SELECT e FROM Emprestimo e LEFT JOIN FETCH e.pagamentos WHERE e.id = :id")
    Optional<Emprestimo> findByIdWithPagamentos(Long id);

    @Query("SELECT e FROM Emprestimo e LEFT JOIN FETCH e.garantias WHERE e.id = :id")
    Optional<Emprestimo> findByIdWithGarantias(Long id);

    @Query("SELECT SUM(e.valorEmprestimo) FROM Emprestimo e WHERE e.cliente.id = :clienteId AND e.status IN :statuses")
    BigDecimal sumValorEmprestimoByClienteIdAndStatusIn(Long clienteId, List<StatusEmprestimo> statuses);

    @Query("SELECT e FROM Emprestimo e WHERE e.dataAprovacao BETWEEN :dataInicio AND :dataFim")
    List<Emprestimo> findByDataAprovacaoBetween(LocalDate dataInicio, LocalDate dataFim);
}
