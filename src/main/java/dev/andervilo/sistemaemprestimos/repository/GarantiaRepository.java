package dev.andervilo.sistemaemprestimos.repository;

import dev.andervilo.sistemaemprestimos.domain.entity.Garantia;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoGarantia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GarantiaRepository extends JpaRepository<Garantia, Long> {

    List<Garantia> findByEmprestimoId(Long emprestimoId);

    List<Garantia> findByTipoGarantia(TipoGarantia tipoGarantia);

    @Query("SELECT g FROM Garantia g WHERE g.emprestimo.id = :emprestimoId")
    List<Garantia> findAllByEmprestimoId(Long emprestimoId);

    @Query("SELECT SUM(g.valorAvaliado) FROM Garantia g WHERE g.emprestimo.id = :emprestimoId")
    BigDecimal sumValorAvaliadoByEmprestimoId(Long emprestimoId);

    @Query("SELECT g FROM Garantia g WHERE g.valorAvaliado >= :valorMinimo")
    List<Garantia> findByValorAvaliadoGreaterThanEqual(BigDecimal valorMinimo);

    @Query("SELECT COUNT(g) FROM Garantia g WHERE g.emprestimo.id = :emprestimoId")
    Long countByEmprestimoId(Long emprestimoId);
}
