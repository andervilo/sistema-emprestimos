package dev.andervilo.sistemaemprestimos.usecase.garantia;

import dev.andervilo.sistemaemprestimos.repository.GarantiaRepository;
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
class DeletarGarantiaUseCaseTest {

    @Mock private GarantiaRepository repository;
    @InjectMocks private DeletarGarantiaUseCase useCase;

    @Test
    @DisplayName("Deve deletar uma garantia com sucesso")
    void deveDeletarGarantiaComSucesso() {
        // Arrange
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        // Act
        useCase.execute(id);

        // Assert
        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar garantia inexistente")
    void deveLancarExcecaoParaGarantiaInexistente() {
        // Arrange
        Long id = 99L;
        when(repository.existsById(id)).thenReturn(false);

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> useCase.execute(id));
        assertEquals("Garantia não encontrada", exception.getMessage());
        verify(repository).existsById(id);
        verify(repository, never()).deleteById(anyLong());
    }
}
