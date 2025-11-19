package dev.andervilo.sistemaemprestimos.usecase.garantia;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.entity.Garantia;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoGarantia;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.dto.GarantiaRequestDTO;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class GarantiaCudUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired private CriarGarantiaUseCase criarGarantiaUseCase;
    @Autowired private AtualizarGarantiaUseCase atualizarGarantiaUseCase;
    @Autowired private DeletarGarantiaUseCase deletarGarantiaUseCase;
    @Autowired private GarantiaRepository garantiaRepository;
    @Autowired private EmprestimoRepository emprestimoRepository;
    @Autowired private ClienteRepository clienteRepository;

    private Emprestimo emprestimo;

    @BeforeEach
    void setUp() {
        garantiaRepository.deleteAll();
        emprestimoRepository.deleteAll();
        clienteRepository.deleteAll();

        Cliente cliente = clienteRepository.save(new Cliente(null, "Cliente Garantia", "555.555.555-55", TipoPessoa.FISICA, null, null, null, null, null, null, null, null, null, null, null, null, null));
        emprestimo = new Emprestimo();
        emprestimo.setCliente(cliente);
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setValorEmprestimo(BigDecimal.TEN);
        emprestimo.setTaxaJuros(BigDecimal.ONE);
        emprestimo.setNumeroParcelas(1);
        emprestimo.setValorParcela(BigDecimal.TEN);
        emprestimo = emprestimoRepository.save(emprestimo);
    }

    @Test
    @DisplayName("Deve criar, atualizar e deletar uma garantia")
    void deveRealizarCicloDeVidaDaGarantia() {
        // 1. Criar
        GarantiaRequestDTO createRequest = new GarantiaRequestDTO();
        createRequest.setEmprestimoId(emprestimo.getId());
        createRequest.setTipoGarantia(TipoGarantia.VEICULO);
        createRequest.setDescricao("Carro 2022");
        createRequest.setValorAvaliado(new BigDecimal("80000.00"));

        GarantiaResponseDTO createdResponse = criarGarantiaUseCase.execute(createRequest);
        assertNotNull(createdResponse);
        assertNotNull(createdResponse.getId());
        assertEquals("Carro 2022", createdResponse.getDescricao());

        Long garantiaId = createdResponse.getId();
        assertTrue(garantiaRepository.existsById(garantiaId));

        // 2. Atualizar
        GarantiaRequestDTO updateRequest = new GarantiaRequestDTO();
        updateRequest.setEmprestimoId(emprestimo.getId()); // Emprestimo ID não é atualizável, mas o DTO exige
        updateRequest.setTipoGarantia(TipoGarantia.VEICULO);
        updateRequest.setDescricao("Carro 2023, modelo novo");
        updateRequest.setValorAvaliado(new BigDecimal("95000.00"));

        GarantiaResponseDTO updatedResponse = atualizarGarantiaUseCase.execute(garantiaId, updateRequest);
        assertNotNull(updatedResponse);
        assertEquals("Carro 2023, modelo novo", updatedResponse.getDescricao());
        assertEquals(0, new BigDecimal("95000.00").compareTo(updatedResponse.getValorAvaliado()));

        Garantia garantiaAtualizada = garantiaRepository.findById(garantiaId).orElseThrow();
        assertEquals("Carro 2023, modelo novo", garantiaAtualizada.getDescricao());

        // 3. Deletar
        deletarGarantiaUseCase.execute(garantiaId);
        assertFalse(garantiaRepository.existsById(garantiaId));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar garantia para empréstimo inexistente")
    void deveLancarExcecaoAoCriarParaEmprestimoInexistente() {
        GarantiaRequestDTO request = new GarantiaRequestDTO();
        request.setEmprestimoId(9999L);
        request.setTipoGarantia(TipoGarantia.IMOVEL);
        request.setDescricao("Inexistente");

        var exception = assertThrows(IllegalArgumentException.class, () -> {
            criarGarantiaUseCase.execute(request);
        });
        assertEquals("Empréstimo não encontrado", exception.getMessage());
    }
}
