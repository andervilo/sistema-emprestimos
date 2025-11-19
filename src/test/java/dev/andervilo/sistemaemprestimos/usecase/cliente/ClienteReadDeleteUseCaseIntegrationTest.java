package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.dto.ClienteResponseDTO;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import dev.andervilo.sistemaemprestimos.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class ClienteReadDeleteUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired private BuscarClientePorIdUseCase buscarClientePorIdUseCase;
    @Autowired private BuscarClientePorCpfCnpjUseCase buscarClientePorCpfCnpjUseCase;
    @Autowired private ListarClientesUseCase listarClientesUseCase;
    @Autowired private DeletarClienteUseCase deletarClienteUseCase;
    @Autowired private ClienteRepository clienteRepository;

    private Cliente cliente1;
    private Cliente cliente2;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
        cliente1 = new Cliente();
        cliente1.setNome("João");
        cliente1.setCpfCnpj("111.111.111-11");
        cliente1.setTipoPessoa(TipoPessoa.FISICA);
        cliente1 = clienteRepository.save(cliente1);

        cliente2 = new Cliente();
        cliente2.setNome("Maria");
        cliente2.setCpfCnpj("222.222.222-22");
        cliente2.setTipoPessoa(TipoPessoa.FISICA);
        cliente2 = clienteRepository.save(cliente2);
    }

    @Test
    @DisplayName("Deve buscar um cliente por ID com sucesso")
    void deveBuscarClientePorId() {
        ClienteResponseDTO result = buscarClientePorIdUseCase.execute(cliente1.getId());
        assertNotNull(result);
        assertEquals(cliente1.getId(), result.getId());
        assertEquals("João", result.getNome());
    }

    @Test
    @DisplayName("Deve buscar um cliente por CPF/CNPJ com sucesso")
    void deveBuscarClientePorCpfCnpj() {
        ClienteResponseDTO result = buscarClientePorCpfCnpjUseCase.execute("222.222.222-22");
        assertNotNull(result);
        assertEquals(cliente2.getId(), result.getId());
        assertEquals("Maria", result.getNome());
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void deveListarClientes() {
        List<ClienteResponseDTO> result = listarClientesUseCase.execute();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Deve deletar um cliente com sucesso")
    void deveDeletarCliente() {
        // Arrange
        long initialCount = clienteRepository.count();
        assertTrue(clienteRepository.existsById(cliente1.getId()));

        // Act
        deletarClienteUseCase.execute(cliente1.getId());

        // Assert
        assertEquals(initialCount - 1, clienteRepository.count());
        assertFalse(clienteRepository.existsById(cliente1.getId()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar cliente inexistente")
    void deveLancarExcecaoAoDeletarClienteInexistente() {
        Long idInexistente = 9999L;
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            deletarClienteUseCase.execute(idInexistente);
        });
        assertEquals("Cliente não encontrado", exception.getMessage());
    }
}
