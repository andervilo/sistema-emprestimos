package dev.andervilo.sistemaemprestimos.usecase.emprestimo;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoRequestDTO;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class SolicitarEmprestimoUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired private SolicitarEmprestimoUseCase solicitarEmprestimoUseCase;
    @Autowired private EmprestimoRepository emprestimoRepository;
    @Autowired private ClienteRepository clienteRepository;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        emprestimoRepository.deleteAll();
        clienteRepository.deleteAll();

        cliente = new Cliente();
        cliente.setNome("Cliente Para Empréstimo");
        cliente.setCpfCnpj("555.555.555-55");
        cliente.setTipoPessoa(TipoPessoa.FISICA);
        cliente = clienteRepository.save(cliente);
    }

    @Test
    @DisplayName("Deve solicitar um empréstimo e salvá-lo no banco de dados")
    void deveSolicitarEmprestimoComSucesso() {
        // Arrange
        EmprestimoRequestDTO requestDTO = new EmprestimoRequestDTO();
        requestDTO.setClienteId(cliente.getId());
        requestDTO.setValorEmprestimo(new BigDecimal("5000.00"));
        requestDTO.setTaxaJuros(new BigDecimal("3.0"));
        requestDTO.setNumeroParcelas(12);
        requestDTO.setFinalidade("Teste de integração");

        // Act
        EmprestimoResponseDTO responseDTO = solicitarEmprestimoUseCase.execute(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getId());
        assertEquals(StatusEmprestimo.PENDENTE_ANALISE, responseDTO.getStatus());

        Emprestimo emprestimoSalvo = emprestimoRepository.findById(responseDTO.getId()).orElseThrow();
        assertEquals(cliente.getId(), emprestimoSalvo.getCliente().getId());
        assertEquals(0, new BigDecimal("5000.00").compareTo(emprestimoSalvo.getValorEmprestimo()));
        assertEquals("Teste de integração", emprestimoSalvo.getFinalidade());

        // Valor da parcela: [5000 * 0.03 * (1.03)^12] / [(1.03)^12 - 1] ~= 502.31
        BigDecimal valorParcelaEsperado = new BigDecimal("502.31");
        assertEquals(0, valorParcelaEsperado.compareTo(emprestimoSalvo.getValorParcela()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao solicitar empréstimo para cliente inexistente")
    void deveLancarExcecaoParaClienteInexistente() {
        // Arrange
        EmprestimoRequestDTO requestDTO = new EmprestimoRequestDTO();
        requestDTO.setClienteId(9999L); // ID Inexistente
        requestDTO.setValorEmprestimo(new BigDecimal("1000.00"));
        requestDTO.setTaxaJuros(new BigDecimal("1.0"));
        requestDTO.setNumeroParcelas(10);

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            solicitarEmprestimoUseCase.execute(requestDTO);
        });
        assertEquals("Cliente não encontrado", exception.getMessage());
    }
}
