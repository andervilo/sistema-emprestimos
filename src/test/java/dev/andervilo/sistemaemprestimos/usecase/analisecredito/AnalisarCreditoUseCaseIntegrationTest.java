package dev.andervilo.sistemaemprestimos.usecase.analisecredito;

import dev.andervilo.sistemaemprestimos.domain.entity.AnaliseCredito;
import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.RecomendacaoAnalise;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoResponseDTO;
import dev.andervilo.sistemaemprestimos.repository.AnaliseCreditoRepository;
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
class AnalisarCreditoUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired private AnalisarCreditoUseCase analisarCreditoUseCase;
    @Autowired private AnaliseCreditoRepository analiseCreditoRepository;
    @Autowired private EmprestimoRepository emprestimoRepository;
    @Autowired private ClienteRepository clienteRepository;

    private Cliente cliente;
    private Emprestimo emprestimo;

    @BeforeEach
    void setUp() {
        analiseCreditoRepository.deleteAll();
        emprestimoRepository.deleteAll();
        clienteRepository.deleteAll();

        cliente = clienteRepository.save(new Cliente(null, "Cliente Análise", "333.333.333-33", TipoPessoa.FISICA, null, null, null, null, null, null, null, null, null, null, null, null, null));
        emprestimo = new Emprestimo();
        emprestimo.setCliente(cliente);
        emprestimo.setStatus(StatusEmprestimo.PENDENTE_ANALISE);
        emprestimo.setValorEmprestimo(BigDecimal.TEN);
        emprestimo.setTaxaJuros(BigDecimal.ONE);
        emprestimo.setNumeroParcelas(1);
        emprestimo.setValorParcela(BigDecimal.TEN);
        emprestimo = emprestimoRepository.save(emprestimo);
    }

    @Test
    @DisplayName("Deve criar uma análise de crédito e atualizar o status do empréstimo")
    void deveCriarAnaliseEAtualizarStatusEmprestimo() {
        // Arrange
        AnaliseCreditoRequestDTO requestDTO = new AnaliseCreditoRequestDTO();
        requestDTO.setClienteId(cliente.getId());
        requestDTO.setEmprestimoId(emprestimo.getId());
        requestDTO.setScoreCredito(800);
        requestDTO.setRecomendacao(RecomendacaoAnalise.APROVAR);
        requestDTO.setAnalistaResponsavel("Analista Teste");

        // Act
        AnaliseCreditoResponseDTO responseDTO = analisarCreditoUseCase.execute(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getId());

        // Verifica a análise salva
        AnaliseCredito analiseSalva = analiseCreditoRepository.findById(responseDTO.getId()).orElseThrow();
        assertEquals(cliente.getId(), analiseSalva.getCliente().getId());
        assertEquals(emprestimo.getId(), analiseSalva.getEmprestimo().getId());
        assertEquals(800, analiseSalva.getScoreCredito());
        assertEquals("Analista Teste", analiseSalva.getAnalistaResponsavel());

        // Verifica a atualização do status do empréstimo
        Emprestimo emprestimoAtualizado = emprestimoRepository.findById(emprestimo.getId()).orElseThrow();
        assertEquals(StatusEmprestimo.EM_ANALISE, emprestimoAtualizado.getStatus());
    }

    @Test
    @DisplayName("Deve criar uma análise de crédito geral para um cliente (sem empréstimo)")
    void deveCriarAnaliseGeralParaCliente() {
        // Arrange
        AnaliseCreditoRequestDTO requestDTO = new AnaliseCreditoRequestDTO();
        requestDTO.setClienteId(cliente.getId());
        requestDTO.setEmprestimoId(null); // Sem empréstimo
        requestDTO.setScoreCredito(720);
        requestDTO.setRecomendacao(RecomendacaoAnalise.SOLICITAR_MAIS_INFORMACOES);

        // Act
        AnaliseCreditoResponseDTO responseDTO = analisarCreditoUseCase.execute(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        AnaliseCredito analiseSalva = analiseCreditoRepository.findById(responseDTO.getId()).orElseThrow();
        assertEquals(cliente.getId(), analiseSalva.getCliente().getId());
        assertNull(analiseSalva.getEmprestimo());
        assertEquals(720, analiseSalva.getScoreCredito());
    }
}
