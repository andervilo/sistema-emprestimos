package dev.andervilo.sistemaemprestimos.usecase.pagamento;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.entity.Pagamento;
import dev.andervilo.sistemaemprestimos.domain.enums.FormaPagamento;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.dto.PagamentoRequestDTO;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class EfetuarPagamentoUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired private EfetuarPagamentoUseCase efetuarPagamentoUseCase;
    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private EmprestimoRepository emprestimoRepository;
    @Autowired private ClienteRepository clienteRepository;

    private Cliente cliente;
    private Emprestimo emprestimo;

    @BeforeEach
    void setUp() {
        pagamentoRepository.deleteAll();
        emprestimoRepository.deleteAll();
        clienteRepository.deleteAll();

        cliente = new Cliente();
        cliente.setNome("Cliente Pagamento");
        cliente.setCpfCnpj("666.666.666-66");
        cliente.setTipoPessoa(TipoPessoa.FISICA);
        cliente = clienteRepository.save(cliente);

        emprestimo = new Emprestimo();
        emprestimo.setCliente(cliente);
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setValorEmprestimo(new BigDecimal("1000.00"));
        emprestimo.setTaxaJuros(new BigDecimal("2.0"));
        emprestimo.setNumeroParcelas(10);
        emprestimo.setValorParcela(new BigDecimal("110.00"));
        emprestimo = emprestimoRepository.save(emprestimo);
    }

    @Test
    @DisplayName("Deve efetuar um pagamento com sucesso")
    void deveEfetuarPagamentoComSucesso() {
        // Arrange
        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO();
        requestDTO.setEmprestimoId(emprestimo.getId());
        requestDTO.setDataPagamento(LocalDate.now());
        requestDTO.setValorPago(new BigDecimal("110.00"));
        requestDTO.setFormaPagamento(FormaPagamento.PIX);
        requestDTO.setNumeroParcela(1);
        requestDTO.setObservacoes("Primeira parcela");

        // Act
        PagamentoResponseDTO responseDTO = efetuarPagamentoUseCase.execute(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getId());
        assertEquals(emprestimo.getId(), responseDTO.getEmprestimoId());
        assertEquals(0, new BigDecimal("110.00").compareTo(responseDTO.getValorPago()));
        assertEquals(FormaPagamento.PIX, responseDTO.getFormaPagamento());
        assertEquals(1, responseDTO.getNumeroParcela());

        // Verificar no banco
        Pagamento pagamentoSalvo = pagamentoRepository.findById(responseDTO.getId()).orElseThrow();
        assertEquals(emprestimo.getId(), pagamentoSalvo.getEmprestimo().getId());
        assertNotNull(pagamentoSalvo.getDataRegistro());
    }

    @Test
    @DisplayName("Deve marcar empréstimo como PAGO quando valor total for pago")
    void deveMarcareEmprestimoComoPagoQuandoTotalForPago() {
        // Arrange - Pagar todas as 10 parcelas
        BigDecimal valorParcela = new BigDecimal("110.00");
        
        for (int i = 1; i <= 10; i++) {
            PagamentoRequestDTO requestDTO = new PagamentoRequestDTO();
            requestDTO.setEmprestimoId(emprestimo.getId());
            requestDTO.setDataPagamento(LocalDate.now().plusDays(i));
            requestDTO.setValorPago(valorParcela);
            requestDTO.setFormaPagamento(FormaPagamento.DEBITO_AUTOMATICO);
            requestDTO.setNumeroParcela(i);

            // Act
            efetuarPagamentoUseCase.execute(requestDTO);
        }

        // Assert
        Emprestimo emprestimoAtualizado = emprestimoRepository.findById(emprestimo.getId()).orElseThrow();
        assertEquals(StatusEmprestimo.PAGO, emprestimoAtualizado.getStatus());
    }

    @Test
    @DisplayName("Deve permitir pagamento parcial sem marcar como PAGO")
    void devePermitirPagamentoParcial() {
        // Arrange
        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO();
        requestDTO.setEmprestimoId(emprestimo.getId());
        requestDTO.setDataPagamento(LocalDate.now());
        requestDTO.setValorPago(new BigDecimal("50.00")); // Valor menor que a parcela
        requestDTO.setFormaPagamento(FormaPagamento.BOLETO);

        // Act
        PagamentoResponseDTO responseDTO = efetuarPagamentoUseCase.execute(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        Emprestimo emprestimoAtualizado = emprestimoRepository.findById(emprestimo.getId()).orElseThrow();
        assertEquals(StatusEmprestimo.ATIVO, emprestimoAtualizado.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar efetuar pagamento para empréstimo inexistente")
    void deveLancarExcecaoParaEmprestimoInexistente() {
        // Arrange
        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO();
        requestDTO.setEmprestimoId(9999L);
        requestDTO.setDataPagamento(LocalDate.now());
        requestDTO.setValorPago(new BigDecimal("100.00"));
        requestDTO.setFormaPagamento(FormaPagamento.PIX);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            efetuarPagamentoUseCase.execute(requestDTO);
        });
        assertEquals("Empréstimo não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar efetuar pagamento para empréstimo com status inválido")
    void deveLancarExcecaoParaEmprestimoComStatusInvalido() {
        // Arrange - Criar empréstimo com status PENDENTE_ANALISE
        Emprestimo emprestimoPendente = new Emprestimo();
        emprestimoPendente.setCliente(cliente);
        emprestimoPendente.setStatus(StatusEmprestimo.PENDENTE_ANALISE);
        emprestimoPendente.setValorEmprestimo(new BigDecimal("500.00"));
        emprestimoPendente.setTaxaJuros(new BigDecimal("1.5"));
        emprestimoPendente.setNumeroParcelas(5);
        emprestimoPendente.setValorParcela(new BigDecimal("105.00"));
        emprestimoPendente = emprestimoRepository.save(emprestimoPendente);

        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO();
        requestDTO.setEmprestimoId(emprestimoPendente.getId());
        requestDTO.setDataPagamento(LocalDate.now());
        requestDTO.setValorPago(new BigDecimal("105.00"));
        requestDTO.setFormaPagamento(FormaPagamento.PIX);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            efetuarPagamentoUseCase.execute(requestDTO);
        });
        assertEquals("Empréstimo não está em situação que permite pagamentos", exception.getMessage());
    }

    @Test
    @DisplayName("Deve efetuar pagamento para empréstimo ATRASADO")
    void deveEfetuarPagamentoParaEmprestimoAtrasado() {
        // Arrange
        emprestimo.setStatus(StatusEmprestimo.ATRASADO);
        emprestimo = emprestimoRepository.save(emprestimo);

        PagamentoRequestDTO requestDTO = new PagamentoRequestDTO();
        requestDTO.setEmprestimoId(emprestimo.getId());
        requestDTO.setDataPagamento(LocalDate.now());
        requestDTO.setValorPago(new BigDecimal("110.00"));
        requestDTO.setFormaPagamento(FormaPagamento.TRANSFERENCIA);
        requestDTO.setNumeroParcela(1);

        // Act
        PagamentoResponseDTO responseDTO = efetuarPagamentoUseCase.execute(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(emprestimo.getId(), responseDTO.getEmprestimoId());
    }
}
