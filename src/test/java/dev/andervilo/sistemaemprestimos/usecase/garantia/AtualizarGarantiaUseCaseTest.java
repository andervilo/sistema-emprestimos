package dev.andervilo.sistemaemprestimos.usecase.garantia;

import dev.andervilo.sistemaemprestimos.domain.entity.Garantia;
import dev.andervilo.sistemaemprestimos.dto.GarantiaRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.GarantiaResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.GarantiaMapper;
import dev.andervilo.sistemaemprestimos.repository.GarantiaRepository;
import org.junit.jupiter.api.BeforeEach;
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
class AtualizarGarantiaUseCaseTest {

    @Mock private GarantiaRepository garantiaRepository;
    @Mock private GarantiaMapper garantiaMapper;

    @InjectMocks private AtualizarGarantiaUseCase atualizarGarantiaUseCase;

    private Garantia garantiaExistente;
    private GarantiaRequestDTO requestDTO;
    private GarantiaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        garantiaExistente = new Garantia();
        garantiaExistente.setId(20L);
        garantiaExistente.setDescricao("Descrição antiga");

        requestDTO = new GarantiaRequestDTO();
        requestDTO.setDescricao("Descrição nova");

        responseDTO = new GarantiaResponseDTO();
        responseDTO.setId(20L);
        responseDTO.setDescricao("Descrição nova");
    }

    @Test
    @DisplayName("Deve atualizar uma garantia com sucesso")
    void deveAtualizarGarantiaComSucesso() {
        // Arrange
        when(garantiaRepository.findById(20L)).thenReturn(Optional.of(garantiaExistente));
        when(garantiaRepository.save(any(Garantia.class))).thenReturn(garantiaExistente);
        when(garantiaMapper.toResponseDTO(garantiaExistente)).thenReturn(responseDTO);
        doNothing().when(garantiaMapper).updateEntityFromDTO(requestDTO, garantiaExistente);

        // Act
        GarantiaResponseDTO result = atualizarGarantiaUseCase.execute(20L, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getDescricao(), result.getDescricao());

        verify(garantiaRepository).findById(20L);
        verify(garantiaMapper).updateEntityFromDTO(requestDTO, garantiaExistente);
        verify(garantiaRepository).save(garantiaExistente);
        verify(garantiaMapper).toResponseDTO(garantiaExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar garantia inexistente")
    void deveLancarExcecaoParaGarantiaInexistente() {
        // Arrange
        when(garantiaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> atualizarGarantiaUseCase.execute(99L, requestDTO));
        assertEquals("Garantia não encontrada", exception.getMessage());
        verify(garantiaRepository).findById(99L);
        verify(garantiaRepository, never()).save(any());
    }
}
