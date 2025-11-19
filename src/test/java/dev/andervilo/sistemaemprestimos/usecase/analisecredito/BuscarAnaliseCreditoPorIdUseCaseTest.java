package dev.andervilo.sistemaemprestimos.usecase.analisecredito;

import dev.andervilo.sistemaemprestimos.domain.entity.AnaliseCredito;
import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.AnaliseCreditoMapper;
import dev.andervilo.sistemaemprestimos.repository.AnaliseCreditoRepository;
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
class BuscarAnaliseCreditoPorIdUseCaseTest {

    @Mock private AnaliseCreditoRepository repository;
    @Mock private AnaliseCreditoMapper mapper;
    @InjectMocks private BuscarAnaliseCreditoPorIdUseCase useCase;

    @Test
    @DisplayName("Deve buscar uma análise de crédito por ID com sucesso")
    void deveBuscarAnalisePorIdComSucesso() {
        // Arrange
        Long id = 1L;
        AnaliseCredito analise = new AnaliseCredito();
        analise.setId(id);
        AnaliseCreditoResponseDTO responseDTO = new AnaliseCreditoResponseDTO();
        responseDTO.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(analise));
        when(mapper.toResponseDTO(analise)).thenReturn(responseDTO);

        // Act
        AnaliseCreditoResponseDTO result = useCase.execute(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(repository).findById(id);
        verify(mapper).toResponseDTO(analise);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar análise com ID inexistente")
    void deveLancarExcecaoParaIdInexistente() {
        // Arrange
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> useCase.execute(id));
        assertEquals("Análise de crédito não encontrada", exception.getMessage());
        verify(repository).findById(id);
        verify(mapper, never()).toResponseDTO(any());
    }
}
