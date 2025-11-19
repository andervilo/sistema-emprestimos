package dev.andervilo.sistemaemprestimos.usecase.emprestimo;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import dev.andervilo.sistemaemprestimos.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class AprovarReprovarEmprestimoUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired private AprovarEmprestimoUseCase aprovarEmprestimoUseCase;
    @Autowired private ReprovarEmprestimoUseCase reprovarEmprestimoUseCase;
    @Autowired private EmprestimoRepository emprestimoRepository;
    @Autowired private ClienteRepository clienteRepository;

    private Emprestimo emprestimoPendente;

    @BeforeEach
    void setUp() {
        emprestimoRepository.deleteAll();
        clienteRepository.deleteAll();

        Cliente cliente = new Cliente();
        cliente.setCpfCnpj("123.123.123-12");
        cliente.setNome("Cliente Aprovação");
        cliente.setTipoPessoa(TipoPessoa.FISICA);
        cliente = clienteRepository.save(cliente);

        emprestimoPendente = new Emprestimo();
        emprestimoPendente.setCliente(cliente);
        emprestimoPendente.setStatus(StatusEmprestimo.PENDENTE_ANALISE);
        emprestimoPendente.setValorEmprestimo(BigDecimal.TEN);
        emprestimoPendente.setTaxaJuros(BigDecimal.ONE);
        emprestimoPendente.setNumeroParcelas(1);
        emprestimoPendente.setValorParcela(BigDecimal.TEN);
        emprestimoPendente = emprestimoRepository.save(emprestimoPendente);
    }

    @Test
    @DisplayName("Deve aprovar um empréstimo pendente")
    void deveAprovarEmprestimo() {
        // Arrange
        LocalDate dataVencimento = LocalDate.now().plusDays(30);

        // Act
        aprovarEmprestimoUseCase.execute(emprestimoPendente.getId(), dataVencimento);

        // Assert
        Emprestimo emprestimoAprovado = emprestimoRepository.findById(emprestimoPendente.getId()).orElseThrow();
        assertEquals(StatusEmprestimo.ATIVO, emprestimoAprovado.getStatus());
        assertEquals(LocalDate.now(), emprestimoAprovado.getDataAprovacao());
        assertEquals(dataVencimento, emprestimoAprovado.getDataVencimentoPrimeiraParcela());
    }

    @Test
    @DisplayName("Deve reprovar um empréstimo pendente")
    void deveReprovarEmprestimo() {
        // Act
        reprovarEmprestimoUseCase.execute(emprestimoPendente.getId());

        // Assert
        Emprestimo emprestimoReprovado = emprestimoRepository.findById(emprestimoPendente.getId()).orElseThrow();
        assertEquals(StatusEmprestimo.REPROVADO, emprestimoReprovado.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar aprovar empréstimo já ativo")
    void deveLancarExcecaoAoAprovarEmprestimoAtivo() {
        // Arrange
        emprestimoPendente.setStatus(StatusEmprestimo.ATIVO);
        emprestimoRepository.save(emprestimoPendente);

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, () -> {
            aprovarEmprestimoUseCase.execute(emprestimoPendente.getId(), LocalDate.now());
        });
        assertEquals("Empréstimo não pode ser aprovado no status atual", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar reprovar empréstimo já reprovado")
    void deveLancarExcecaoAoReprovarEmprestimoReprovado() {
        // Arrange
        emprestimoPendente.setStatus(StatusEmprestimo.REPROVADO);
        emprestimoRepository.save(emprestimoPendente);

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, () -> {
            reprovarEmprestimoUseCase.execute(emprestimoPendente.getId());
        });
        assertEquals("Empréstimo não pode ser reprovado no status atual", exception.getMessage());
    }
}
