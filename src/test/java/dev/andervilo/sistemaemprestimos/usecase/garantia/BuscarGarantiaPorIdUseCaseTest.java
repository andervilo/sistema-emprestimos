package dev.andervilo.sistemaemprestimos.usecase.garantia;

import dev.andervilo.sistemaemprestimos.domain.entity.Garantia;
import dev.andervilo.sistemaemprestimos.dto.GarantiaResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.GarantiaMapper;
import dev.andervilo.sistemaemprestimos.repository.GarantiaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarGarantiaPorIdUseCaseTest {

    @Mock private GarantiaRepository repository;
    @Mock private GarantiaMapper mapper;
    @InjectMocks private BuscarGarantiaPorIdUseCase useCase;

    @Test
    @DisplayName("Deve buscar uma garantia por ID com sucesso")
    void deveBuscarGarantiaPorIdComSucesso() {
        // Arrange
        Long id = 1L;
        Garantia garantia = new Garantia();
        garantia.setId(id);
        GarantiaResponseDTO responseDTO = new GarantiaResponseDTO();
        responseDTO.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(garantia));
        when(mapper.toResponseDTO(garantia)).thenReturn(responseDTO);

        // Act
        GarantiaResponseDTO result = useCase.execute(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(repository).findById(id);
        verify(mapper).toResponseDTO(garantia);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar garantia com ID inexistente")
    void deveLancarExcecaoParaIdInexistente() {
        // Arrange
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> useCase.execute(id));
        assertEquals("Garantia não encontrada", exception.getMessage());
        verify(repository).findById(id);
        verify(mapper, never()).toResponseDTO(any());
    }
}
