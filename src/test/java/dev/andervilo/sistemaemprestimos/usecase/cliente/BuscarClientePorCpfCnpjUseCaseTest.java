package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.dto.ClienteResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.ClienteMapper;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarClientePorCpfCnpjUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private BuscarClientePorCpfCnpjUseCase buscarClientePorCpfCnpjUseCase;

    private Cliente cliente;
    private ClienteResponseDTO responseDTO;
    private final String cpfCnpj = "123.456.789-00";

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João da Silva");
        cliente.setCpfCnpj(cpfCnpj);

        responseDTO = new ClienteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("João da Silva");
        responseDTO.setCpfCnpj(cpfCnpj);
    }

    @Test
    @DisplayName("Deve buscar um cliente por CPF/CNPJ com sucesso")
    void deveBuscarClientePorCpfCnpjComSucesso() {
        // Arrange
        when(clienteRepository.findByCpfCnpj(cpfCnpj)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO result = buscarClientePorCpfCnpjUseCase.execute(cpfCnpj);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getCpfCnpj(), result.getCpfCnpj());
        verify(clienteRepository).findByCpfCnpj(cpfCnpj);
        verify(clienteMapper).toResponseDTO(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente com CPF/CNPJ inexistente")
    void deveLancarExcecaoAoBuscarClienteComCpfCnpjInexistente() {
        // Arrange
        String cpfCnpjInexistente = "000.000.000-00";
        when(clienteRepository.findByCpfCnpj(cpfCnpjInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            buscarClientePorCpfCnpjUseCase.execute(cpfCnpjInexistente);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteRepository).findByCpfCnpj(cpfCnpjInexistente);
    }
}
