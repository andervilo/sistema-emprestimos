package dev.andervilo.sistemaemprestimos.repository;

import dev.andervilo.sistemaemprestimos.domain.entity.Pagamento;
import dev.andervilo.sistemaemprestimos.domain.enums.FormaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByEmprestimoId(Long emprestimoId);

    List<Pagamento> findByFormaPagamento(FormaPagamento formaPagamento);

    @Query("SELECT p FROM Pagamento p WHERE p.emprestimo.id = :emprestimoId ORDER BY p.dataPagamento DESC")
    List<Pagamento> findByEmprestimoIdOrderByDataPagamentoDesc(Long emprestimoId);

    @Query("SELECT p FROM Pagamento p WHERE p.dataPagamento BETWEEN :dataInicio AND :dataFim")
    List<Pagamento> findByDataPagamentoBetween(LocalDate dataInicio, LocalDate dataFim);

    @Query("SELECT SUM(p.valorPago) FROM Pagamento p WHERE p.emprestimo.id = :emprestimoId")
    BigDecimal sumValorPagoByEmprestimoId(Long emprestimoId);

    @Query("SELECT p FROM Pagamento p WHERE p.emprestimo.cliente.id = :clienteId")
    List<Pagamento> findByClienteId(Long clienteId);

    @Query("SELECT COUNT(p) FROM Pagamento p WHERE p.emprestimo.id = :emprestimoId")
    Long countByEmprestimoId(Long emprestimoId);
}
