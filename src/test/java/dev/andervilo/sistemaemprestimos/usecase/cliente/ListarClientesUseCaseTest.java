package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.dto.ClienteResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.ClienteMapper;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarClientesUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ListarClientesUseCase listarClientesUseCase;

    @Test
    @DisplayName("Deve listar todos os clientes com sucesso")
    void deveListarTodosOsClientesComSucesso() {
        // Arrange
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        List<Cliente> clientes = List.of(cliente1, cliente2);

        ClienteResponseDTO responseDTO1 = new ClienteResponseDTO();
        responseDTO1.setId(1L);
        ClienteResponseDTO responseDTO2 = new ClienteResponseDTO();
        responseDTO2.setId(2L);

        when(clienteRepository.findAll()).thenReturn(clientes);
        when(clienteMapper.toResponseDTO(cliente1)).thenReturn(responseDTO1);
        when(clienteMapper.toResponseDTO(cliente2)).thenReturn(responseDTO2);

        // Act
        List<ClienteResponseDTO> result = listarClientesUseCase.execute();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(clienteRepository).findAll();
        verify(clienteMapper, times(2)).toResponseDTO(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não há clientes")
    void deveRetornarListaVaziaQuandoNaoHaClientes() {
        // Arrange
        when(clienteRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ClienteResponseDTO> result = listarClientesUseCase.execute();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(clienteRepository).findAll();
        verify(clienteMapper, never()).toResponseDTO(any());
    }
}
