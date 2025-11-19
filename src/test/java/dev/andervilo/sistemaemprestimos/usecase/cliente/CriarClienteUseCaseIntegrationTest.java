package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.dto.ClienteRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.ClienteResponseDTO;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import dev.andervilo.sistemaemprestimos.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class CriarClienteUseCaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CriarClienteUseCase criarClienteUseCase;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    @DisplayName("Deve criar um novo cliente no banco de dados")
    void deveCriarClienteComSucesso() {
        // Arrange
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("Cliente de Teste");
        requestDTO.setCpfCnpj("987.654.321-00");
        requestDTO.setTipoPessoa(TipoPessoa.FISICA);
        requestDTO.setEmail("teste@integration.com");

        // Act
        ClienteResponseDTO responseDTO = criarClienteUseCase.execute(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getId());
        assertEquals("Cliente de Teste", responseDTO.getNome());

        // Verify in the database
        Optional<Cliente> clienteSalvoOpt = clienteRepository.findById(responseDTO.getId());
        assertTrue(clienteSalvoOpt.isPresent());
        Cliente clienteSalvo = clienteSalvoOpt.get();
        assertEquals("987.654.321-00", clienteSalvo.getCpfCnpj());
        assertEquals("teste@integration.com", clienteSalvo.getEmail());
        assertNotNull(clienteSalvo.getDataCadastro());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar cliente com CPF/CNPJ duplicado")
    void deveLancarExcecaoAoCriarClienteDuplicado() {
        // Arrange: First, create a client
        Cliente clienteExistente = new Cliente();
        clienteExistente.setNome("Cliente Original");
        clienteExistente.setCpfCnpj("111.222.333-44");
        clienteExistente.setTipoPessoa(TipoPessoa.FISICA);
        clienteRepository.save(clienteExistente);

        // Now, create a request with the same CPF/CNPJ
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("Cliente Duplicado");
        requestDTO.setCpfCnpj("111.222.333-44");
        requestDTO.setTipoPessoa(TipoPessoa.FISICA);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            criarClienteUseCase.execute(requestDTO);
        });

        assertEquals("CPF/CNPJ já cadastrado", exception.getMessage());
    }
}
