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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarAnalisesCreditoPorClienteUseCaseTest {

    @Mock private AnaliseCreditoRepository repository;
    @Mock private AnaliseCreditoMapper mapper;
    @InjectMocks private ListarAnalisesCreditoPorClienteUseCase useCase;

    @Test
    @DisplayName("Deve listar as análises de crédito de um cliente com sucesso")
    void deveListarAnalisesPorClienteComSucesso() {
        // Arrange
        Long clienteId = 1L;
        List<AnaliseCredito> analises = List.of(new AnaliseCredito(), new AnaliseCredito());

        when(repository.findByClienteIdOrderByDataAnaliseDesc(clienteId)).thenReturn(analises);
        when(mapper.toResponseDTO(any(AnaliseCredito.class))).thenReturn(new AnaliseCreditoResponseDTO());

        // Act
        List<AnaliseCreditoResponseDTO> result = useCase.execute(clienteId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findByClienteIdOrderByDataAnaliseDesc(clienteId);
        verify(mapper, times(2)).toResponseDTO(any(AnaliseCredito.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando um cliente não tem análises")
    void deveRetornarListaVaziaParaClienteSemAnalises() {
        // Arrange
        Long clienteId = 2L;
        when(repository.findByClienteIdOrderByDataAnaliseDesc(clienteId)).thenReturn(Collections.emptyList());

        // Act
        List<AnaliseCreditoResponseDTO> result = useCase.execute(clienteId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(repository).findByClienteIdOrderByDataAnaliseDesc(clienteId);
        verify(mapper, never()).toResponseDTO(any());
    }
}
