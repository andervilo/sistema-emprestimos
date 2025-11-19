package dev.andervilo.sistemaemprestimos.usecase.garantia;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.entity.Garantia;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoGarantia;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.dto.GarantiaResponseDTO;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import dev.andervilo.sistemaemprestimos.repository.GarantiaRepository;
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
class GarantiaReadUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired private BuscarGarantiaPorIdUseCase buscarGarantiaPorIdUseCase;
    @Autowired private ListarGarantiasPorEmprestimoUseCase listarGarantiasPorEmprestimoUseCase;
    @Autowired private GarantiaRepository garantiaRepository;
    @Autowired private EmprestimoRepository emprestimoRepository;
    @Autowired private ClienteRepository clienteRepository;

    private Emprestimo emprestimo;
    private Garantia garantia1;
    private Garantia garantia2;

    @BeforeEach
    void setUp() {
        garantiaRepository.deleteAll();
        emprestimoRepository.deleteAll();
        clienteRepository.deleteAll();

        Cliente cliente = clienteRepository.save(new Cliente(null, "Cliente Leitura Garantia", "666.666.666-66", TipoPessoa.FISICA, null, null, null, null, null, null, null, null, null, null, null, null, null));
        
        emprestimo = new Emprestimo();
        emprestimo.setCliente(cliente);
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setValorEmprestimo(BigDecimal.TEN);
        emprestimo.setTaxaJuros(BigDecimal.ONE);
        emprestimo.setNumeroParcelas(1);
        emprestimo.setValorParcela(BigDecimal.TEN);
        emprestimo = emprestimoRepository.save(emprestimo);

        garantia1 = new Garantia(null, emprestimo, TipoGarantia.IMOVEL, "Casa", new BigDecimal("300000"), null, null, null);
        garantia1 = garantiaRepository.save(garantia1);

        garantia2 = new Garantia(null, emprestimo, TipoGarantia.VEICULO, "Carro", new BigDecimal("50000"), null, null, null);
        garantia2 = garantiaRepository.save(garantia2);
    }

    @Test
    @DisplayName("Deve buscar uma garantia por ID")
    void deveBuscarGarantiaPorId() {
        GarantiaResponseDTO result = buscarGarantiaPorIdUseCase.execute(garantia1.getId());
        assertNotNull(result);
        assertEquals(garantia1.getId(), result.getId());
        assertEquals("Casa", result.getDescricao());
    }

    @Test
    @DisplayName("Deve listar todas as garantias de um empréstimo")
    void deveListarGarantiasPorEmprestimo() {
        List<GarantiaResponseDTO> result = listarGarantiasPorEmprestimoUseCase.execute(emprestimo.getId());
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para empréstimo sem garantias")
    void deveRetornarListaVaziaParaEmprestimoSemGarantias() {
        Emprestimo emprestimoSemGarantia = new Emprestimo();
        emprestimoSemGarantia.setCliente(emprestimo.getCliente());
        emprestimoSemGarantia.setStatus(StatusEmprestimo.ATIVO);
        emprestimoSemGarantia.setValorEmprestimo(BigDecimal.ONE);
        emprestimoSemGarantia.setTaxaJuros(BigDecimal.ONE);
        emprestimoSemGarantia.setNumeroParcelas(1);
        emprestimoSemGarantia.setValorParcela(BigDecimal.ONE);
        emprestimoSemGarantia = emprestimoRepository.save(emprestimoSemGarantia);

        List<GarantiaResponseDTO> result = listarGarantiasPorEmprestimoUseCase.execute(emprestimoSemGarantia.getId());
        assertTrue(result.isEmpty());
    }
}
