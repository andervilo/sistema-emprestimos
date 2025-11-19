package dev.andervilo.sistemaemprestimos.usecase.emprestimo;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoResponseDTO;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class EmprestimoReadUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired private BuscarEmprestimoPorIdUseCase buscarEmprestimoPorIdUseCase;
    @Autowired private ListarEmprestimosUseCase listarEmprestimosUseCase;
    @Autowired private ListarEmprestimosPorClienteUseCase listarEmprestimosPorClienteUseCase;
    @Autowired private EmprestimoRepository emprestimoRepository;
    @Autowired private ClienteRepository clienteRepository;

    private Cliente cliente1;
    private Cliente cliente2;
    private Emprestimo emprestimo1;
    private Emprestimo emprestimo2;

    @BeforeEach
    void setUp() {
        emprestimoRepository.deleteAll();
        clienteRepository.deleteAll();

        cliente1 = clienteRepository.save(new Cliente(null, "Cliente A", "111.111.111-11", TipoPessoa.FISICA, null, null, null, null, null, null, null, null, null, null, null, null, null));
        cliente2 = clienteRepository.save(new Cliente(null, "Cliente B", "222.222.222-22", TipoPessoa.FISICA, null, null, null, null, null, null, null, null, null, null, null, null, null));

        emprestimo1 = new Emprestimo();
        emprestimo1.setCliente(cliente1);
        emprestimo1.setStatus(StatusEmprestimo.ATIVO);
        emprestimo1.setValorEmprestimo(new BigDecimal("1000"));
        emprestimo1.setTaxaJuros(BigDecimal.ONE);
        emprestimo1.setNumeroParcelas(1);
        emprestimo1.setValorParcela(new BigDecimal("1000"));
        emprestimo1 = emprestimoRepository.save(emprestimo1);

        emprestimo2 = new Emprestimo();
        emprestimo2.setCliente(cliente1); // Mesmo cliente do emprestimo1
        emprestimo2.setStatus(StatusEmprestimo.PAGO);
        emprestimo2.setValorEmprestimo(new BigDecimal("2000"));
        emprestimo2.setTaxaJuros(BigDecimal.ONE);
        emprestimo2.setNumeroParcelas(1);
        emprestimo2.setValorParcela(new BigDecimal("2000"));
        emprestimo2 = emprestimoRepository.save(emprestimo2);
    }

    @Test
    @DisplayName("Deve buscar um empréstimo por ID")
    void deveBuscarEmprestimoPorId() {
        EmprestimoResponseDTO result = buscarEmprestimoPorIdUseCase.execute(emprestimo1.getId());
        assertNotNull(result);
        assertEquals(emprestimo1.getId(), result.getId());
        assertEquals(0, new BigDecimal("1000").compareTo(result.getValorEmprestimo()));
    }

    @Test
    @DisplayName("Deve listar todos os empréstimos")
    void deveListarTodosEmprestimos() {
        List<EmprestimoResponseDTO> result = listarEmprestimosUseCase.execute();
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Deve listar todos os empréstimos de um cliente específico")
    void deveListarEmprestimosPorCliente() {
        List<EmprestimoResponseDTO> result = listarEmprestimosPorClienteUseCase.execute(cliente1.getId());
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para cliente sem empréstimos")
    void deveRetornarListaVaziaParaClienteSemEmprestimos() {
        List<EmprestimoResponseDTO> result = listarEmprestimosPorClienteUseCase.execute(cliente2.getId());
        assertTrue(result.isEmpty());
    }
}
