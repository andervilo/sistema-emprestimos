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
class BuscarClientePorIdUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private BuscarClientePorIdUseCase buscarClientePorIdUseCase;

    private Cliente cliente;
    private ClienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João da Silva");
        cliente.setCpfCnpj("123.456.789-00");

        responseDTO = new ClienteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("João da Silva");
        responseDTO.setCpfCnpj("123.456.789-00");
    }

    @Test
    @DisplayName("Deve buscar um cliente por ID com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO result = buscarClientePorIdUseCase.execute(1L);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        verify(clienteRepository).findById(1L);
        verify(clienteMapper).toResponseDTO(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente com ID inexistente")
    void deveLancarExcecaoAoBuscarClienteComIdInexistente() {
        // Arrange
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            buscarClientePorIdUseCase.execute(99L);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteRepository).findById(99L);
    }
}
