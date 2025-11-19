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
class BuscarAnaliseCreditoPorEmprestimoUseCaseTest {

    @Mock private AnaliseCreditoRepository repository;
    @Mock private AnaliseCreditoMapper mapper;
    @InjectMocks private BuscarAnaliseCreditoPorEmprestimoUseCase useCase;

    @Test
    @DisplayName("Deve buscar uma análise de crédito por ID do empréstimo com sucesso")
    void deveBuscarAnalisePorEmprestimoIdComSucesso() {
        // Arrange
        Long emprestimoId = 10L;
        AnaliseCredito analise = new AnaliseCredito();
        AnaliseCreditoResponseDTO responseDTO = new AnaliseCreditoResponseDTO();
        responseDTO.setEmprestimoId(emprestimoId);

        when(repository.findByEmprestimoId(emprestimoId)).thenReturn(Optional.of(analise));
        when(mapper.toResponseDTO(analise)).thenReturn(responseDTO);

        // Act
        AnaliseCreditoResponseDTO result = useCase.execute(emprestimoId);

        // Assert
        assertNotNull(result);
        assertEquals(emprestimoId, result.getEmprestimoId());
        verify(repository).findByEmprestimoId(emprestimoId);
        verify(mapper).toResponseDTO(analise);
    }

    @Test
    @DisplayName("Deve lançar exceção se não houver análise para o ID do empréstimo")
    void deveLancarExcecaoParaEmprestimoIdSemAnalise() {
        // Arrange
        Long emprestimoId = 99L;
        when(repository.findByEmprestimoId(emprestimoId)).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> useCase.execute(emprestimoId));
        assertEquals("Análise de crédito não encontrada para este empréstimo", exception.getMessage());
        verify(repository).findByEmprestimoId(emprestimoId);
        verify(mapper, never()).toResponseDTO(any());
    }
}
