package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletarClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private DeletarClienteUseCase deletarClienteUseCase;

    @Test
    @DisplayName("Deve deletar um cliente com sucesso")
    void deveDeletarClienteComSucesso() {
        // Arrange
        Long clienteId = 1L;
        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(clienteId);

        // Act
        deletarClienteUseCase.execute(clienteId);

        // Assert
        verify(clienteRepository).existsById(clienteId);
        verify(clienteRepository).deleteById(clienteId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar cliente inexistente")
    void deveLancarExcecaoAoTentarDeletarClienteInexistente() {
        // Arrange
        Long clienteId = 99L;
        when(clienteRepository.existsById(clienteId)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            deletarClienteUseCase.execute(clienteId);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteRepository).existsById(clienteId);
        verify(clienteRepository, never()).deleteById(anyLong());
    }
}
