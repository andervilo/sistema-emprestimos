package dev.andervilo.sistemaemprestimos.repository;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpfCnpj(String cpfCnpj);

    boolean existsByCpfCnpj(String cpfCnpj);

    List<Cliente> findByTipoPessoa(TipoPessoa tipoPessoa);

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT c FROM Cliente c WHERE c.email = :email")
    Optional<Cliente> findByEmail(String email);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.emprestimos WHERE c.id = :id")
    Optional<Cliente> findByIdWithEmprestimos(Long id);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.analises WHERE c.id = :id")
    Optional<Cliente> findByIdWithAnalises(Long id);
}
