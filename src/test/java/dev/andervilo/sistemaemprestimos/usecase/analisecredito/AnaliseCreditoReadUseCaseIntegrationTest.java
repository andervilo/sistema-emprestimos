package dev.andervilo.sistemaemprestimos.usecase.analisecredito;

import dev.andervilo.sistemaemprestimos.domain.entity.AnaliseCredito;
import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.RecomendacaoAnalise;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class AnaliseCreditoReadUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired private BuscarAnaliseCreditoPorIdUseCase buscarAnaliseCreditoPorIdUseCase;
    @Autowired private BuscarAnaliseCreditoPorEmprestimoUseCase buscarAnaliseCreditoPorEmprestimoUseCase;
    @Autowired private ListarAnalisesCreditoPorClienteUseCase listarAnalisesCreditoPorClienteUseCase;
    @Autowired private AnaliseCreditoRepository analiseCreditoRepository;
    @Autowired private EmprestimoRepository emprestimoRepository;
    @Autowired private ClienteRepository clienteRepository;

    private Cliente cliente;
    private Emprestimo emprestimo;
    private AnaliseCredito analise;

    @BeforeEach
    void setUp() {
        analiseCreditoRepository.deleteAll();
        emprestimoRepository.deleteAll();
        clienteRepository.deleteAll();

        cliente = clienteRepository.save(new Cliente(null, "Cliente Leitura", "444.444.444-44", TipoPessoa.FISICA, null, null, null, null, null, null, null, null, null, null, null, null, null));
        
        emprestimo = new Emprestimo();
        emprestimo.setCliente(cliente);
        emprestimo.setStatus(StatusEmprestimo.EM_ANALISE);
        emprestimo.setValorEmprestimo(BigDecimal.TEN);
        emprestimo.setTaxaJuros(BigDecimal.ONE);
        emprestimo.setNumeroParcelas(1);
        emprestimo.setValorParcela(BigDecimal.TEN);
        emprestimo = emprestimoRepository.save(emprestimo);

        analise = new AnaliseCredito();
        analise.setCliente(cliente);
        analise.setEmprestimo(emprestimo);
        analise.setScoreCredito(650);
        analise.setRecomendacao(RecomendacaoAnalise.SOLICITAR_MAIS_INFORMACOES);
        analise = analiseCreditoRepository.save(analise);
    }

    @Test
    @DisplayName("Deve buscar uma análise de crédito por ID")
    void deveBuscarAnalisePorId() {
        AnaliseCreditoResponseDTO result = buscarAnaliseCreditoPorIdUseCase.execute(analise.getId());
        assertNotNull(result);
        assertEquals(analise.getId(), result.getId());
        assertEquals(650, result.getScoreCredito());
    }

    @Test
    @DisplayName("Deve buscar uma análise de crédito por ID do empréstimo")
    void deveBuscarAnalisePorEmprestimoId() {
        AnaliseCreditoResponseDTO result = buscarAnaliseCreditoPorEmprestimoUseCase.execute(emprestimo.getId());
        assertNotNull(result);
        assertEquals(analise.getId(), result.getId());
        assertEquals(emprestimo.getId(), result.getEmprestimoId());
    }

    @Test
    @DisplayName("Deve listar as análises de crédito de um cliente")
    void deveListarAnalisesPorCliente() {
        // Adiciona uma segunda análise para o mesmo cliente
        analiseCreditoRepository.save(new AnaliseCredito(null, cliente, null, 800, null, null, RecomendacaoAnalise.APROVAR, null, null, null));
        
        List<AnaliseCreditoResponseDTO> result = listarAnalisesCreditoPorClienteUseCase.execute(cliente.getId());
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar análise por ID de empréstimo inexistente")
    void deveLancarExcecaoAoBuscarPorEmprestimoIdInexistente() {
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            buscarAnaliseCreditoPorEmprestimoUseCase.execute(9999L);
        });
        assertEquals("Análise de crédito não encontrada para este empréstimo", exception.getMessage());
    }
}
