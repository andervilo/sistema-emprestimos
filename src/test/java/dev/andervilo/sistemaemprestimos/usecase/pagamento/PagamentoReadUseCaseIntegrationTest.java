package dev.andervilo.sistemaemprestimos.usecase.pagamento;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.entity.Pagamento;
import dev.andervilo.sistemaemprestimos.domain.enums.FormaPagamento;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.dto.PagamentoResponseDTO;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import dev.andervilo.sistemaemprestimos.repository.PagamentoRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class PagamentoReadUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired private BuscarPagamentoPorIdUseCase buscarPagamentoPorIdUseCase;
    @Autowired private ListarPagamentosPorEmprestimoUseCase listarPagamentosPorEmprestimoUseCase;
    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private EmprestimoRepository emprestimoRepository;
    @Autowired private ClienteRepository clienteRepository;

    private Cliente cliente;
    private Emprestimo emprestimo;
    private Pagamento pagamento1;
    private Pagamento pagamento2;

    @BeforeEach
    void setUp() {
        pagamentoRepository.deleteAll();
        emprestimoRepository.deleteAll();
        clienteRepository.deleteAll();

        cliente = new Cliente();
        cliente.setNome("Cliente Leitura Pagamento");
        cliente.setCpfCnpj("777.777.777-77");
        cliente.setTipoPessoa(TipoPessoa.FISICA);
        cliente = clienteRepository.save(cliente);

        emprestimo = new Emprestimo();
        emprestimo.setCliente(cliente);
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setValorEmprestimo(new BigDecimal("2000.00"));
        emprestimo.setTaxaJuros(new BigDecimal("2.5"));
        emprestimo.setNumeroParcelas(12);
        emprestimo.setValorParcela(new BigDecimal("180.00"));
        emprestimo = emprestimoRepository.save(emprestimo);

        pagamento1 = new Pagamento();
        pagamento1.setEmprestimo(emprestimo);
        pagamento1.setDataPagamento(LocalDate.now().minusDays(10));
        pagamento1.setValorPago(new BigDecimal("180.00"));
        pagamento1.setFormaPagamento(FormaPagamento.PIX);
        pagamento1.setNumeroParcela(1);
        pagamento1.setObservacoes("Primeira parcela");
        pagamento1 = pagamentoRepository.save(pagamento1);

        pagamento2 = new Pagamento();
        pagamento2.setEmprestimo(emprestimo);
        pagamento2.setDataPagamento(LocalDate.now().minusDays(5));
        pagamento2.setValorPago(new BigDecimal("180.00"));
        pagamento2.setFormaPagamento(FormaPagamento.BOLETO);
        pagamento2.setNumeroParcela(2);
        pagamento2.setObservacoes("Segunda parcela");
        pagamento2 = pagamentoRepository.save(pagamento2);
    }

    @Test
    @DisplayName("Deve buscar um pagamento por ID")
    void deveBuscarPagamentoPorId() {
        // Act
        PagamentoResponseDTO result = buscarPagamentoPorIdUseCase.execute(pagamento1.getId());

        // Assert
        assertNotNull(result);
        assertEquals(pagamento1.getId(), result.getId());
        assertEquals(emprestimo.getId(), result.getEmprestimoId());
        assertEquals(0, new BigDecimal("180.00").compareTo(result.getValorPago()));
        assertEquals(FormaPagamento.PIX, result.getFormaPagamento());
        assertEquals(1, result.getNumeroParcela());
        assertEquals("Primeira parcela", result.getObservacoes());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pagamento por ID inexistente")
    void deveLancarExcecaoAoBuscarPorIdInexistente() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            buscarPagamentoPorIdUseCase.execute(9999L);
        });
        assertEquals("Pagamento não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve listar pagamentos por empréstimo ordenados por data (mais recente primeiro)")
    void deveListarPagamentosPorEmprestimo() {
        // Act
        List<PagamentoResponseDTO> result = listarPagamentosPorEmprestimoUseCase.execute(emprestimo.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verifica ordenação por data de pagamento descendente (mais recente primeiro)
        assertEquals(pagamento2.getId(), result.get(0).getId()); // Mais recente (há 5 dias)
        assertEquals(pagamento1.getId(), result.get(1).getId()); // Mais antigo (há 10 dias)
        
        assertEquals(2, result.get(0).getNumeroParcela());
        assertEquals(1, result.get(1).getNumeroParcela());
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao listar pagamentos de empréstimo sem pagamentos")
    void deveRetornarListaVaziaParaEmprestimoSemPagamentos() {
        // Arrange - Criar empréstimo sem pagamentos
        Emprestimo emprestimoSemPagamentos = new Emprestimo();
        emprestimoSemPagamentos.setCliente(cliente);
        emprestimoSemPagamentos.setStatus(StatusEmprestimo.ATIVO);
        emprestimoSemPagamentos.setValorEmprestimo(new BigDecimal("1000.00"));
        emprestimoSemPagamentos.setTaxaJuros(new BigDecimal("2.0"));
        emprestimoSemPagamentos.setNumeroParcelas(10);
        emprestimoSemPagamentos.setValorParcela(new BigDecimal("110.00"));
        emprestimoSemPagamentos = emprestimoRepository.save(emprestimoSemPagamentos);

        // Act
        List<PagamentoResponseDTO> result = listarPagamentosPorEmprestimoUseCase.execute(emprestimoSemPagamentos.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve listar múltiplos pagamentos com diferentes formas de pagamento")
    void deveListarMultiplosPagamentosComDiferentesFormas() {
        // Arrange - Adicionar mais pagamentos
        Pagamento pagamento3 = new Pagamento();
        pagamento3.setEmprestimo(emprestimo);
        pagamento3.setDataPagamento(LocalDate.now());
        pagamento3.setValorPago(new BigDecimal("180.00"));
        pagamento3.setFormaPagamento(FormaPagamento.DEBITO_AUTOMATICO);
        pagamento3.setNumeroParcela(3);
        pagamentoRepository.save(pagamento3);

        // Act
        List<PagamentoResponseDTO> result = listarPagamentosPorEmprestimoUseCase.execute(emprestimo.getId());

        // Assert
        assertEquals(3, result.size());
        
        // Verifica que todas as formas de pagamento estão presentes
        assertTrue(result.stream().anyMatch(p -> p.getFormaPagamento() == FormaPagamento.PIX));
        assertTrue(result.stream().anyMatch(p -> p.getFormaPagamento() == FormaPagamento.BOLETO));
        assertTrue(result.stream().anyMatch(p -> p.getFormaPagamento() == FormaPagamento.DEBITO_AUTOMATICO));
    }
}
