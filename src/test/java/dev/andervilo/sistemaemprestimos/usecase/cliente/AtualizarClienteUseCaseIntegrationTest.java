package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.dto.ClienteRequestDTO;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import dev.andervilo.sistemaemprestimos.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class AtualizarClienteUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AtualizarClienteUseCase atualizarClienteUseCase;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll(); // Garante um estado limpo
        clienteExistente = new Cliente();
        clienteExistente.setNome("Cliente Original");
        clienteExistente.setCpfCnpj("123.456.789-00");
        clienteExistente.setTipoPessoa(TipoPessoa.FISICA);
        clienteExistente.setEmail("original@teste.com");
        clienteExistente = clienteRepository.save(clienteExistente);
    }

    @Test
    @DisplayName("Deve atualizar os dados de um cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        // Arrange
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("Cliente Atualizado");
        requestDTO.setCpfCnpj("123.456.789-00"); // Mesmo CPF/CNPJ
        requestDTO.setEmail("atualizado@teste.com");
        requestDTO.setTipoPessoa(TipoPessoa.FISICA);

        // Act
        atualizarClienteUseCase.execute(clienteExistente.getId(), requestDTO);

        // Assert
        Cliente clienteAtualizado = clienteRepository.findById(clienteExistente.getId()).orElseThrow();
        assertEquals("Cliente Atualizado", clienteAtualizado.getNome());
        assertEquals("atualizado@teste.com", clienteAtualizado.getEmail());
        assertNotNull(clienteAtualizado.getDataAtualizacao());
        assertTrue(clienteAtualizado.getDataAtualizacao().isAfter(clienteAtualizado.getDataCadastro()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar cliente inexistente")
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        // Arrange
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("Nome qualquer");
        requestDTO.setCpfCnpj("000.000.000-00");
        requestDTO.setTipoPessoa(TipoPessoa.FISICA);

        // Act & Assert
        Long idInexistente = 9999L;
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            atualizarClienteUseCase.execute(idInexistente, requestDTO);
        });
        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao usar CPF/CNPJ de outro cliente na atualização")
    void deveLancarExcecaoAoUsarCpfCnpjDeOutroCliente() {
        // Arrange
        // Cria um segundo cliente
        Cliente outroCliente = new Cliente();
        outroCliente.setNome("Outro Cliente");
        outroCliente.setCpfCnpj("999.888.777-66");
        outroCliente.setTipoPessoa(TipoPessoa.FISICA);
        clienteRepository.save(outroCliente);

        // Tenta atualizar o primeiro cliente com o CPF/CNPJ do segundo
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("Tentativa de Atualização");
        requestDTO.setCpfCnpj("999.888.777-66"); // CPF/CNPJ do outroCliente
        requestDTO.setTipoPessoa(TipoPessoa.FISICA);

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            atualizarClienteUseCase.execute(clienteExistente.getId(), requestDTO);
        });
        assertEquals("CPF/CNPJ já cadastrado para outro cliente", exception.getMessage());
    }
}
