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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarGarantiasPorEmprestimoUseCaseTest {

    @Mock private GarantiaRepository repository;
    @Mock private GarantiaMapper mapper;
    @InjectMocks private ListarGarantiasPorEmprestimoUseCase useCase;

    @Test
    @DisplayName("Deve listar as garantias de um empréstimo com sucesso")
    void deveListarGarantiasPorEmprestimoComSucesso() {
        // Arrange
        Long emprestimoId = 1L;
        List<Garantia> garantias = List.of(new Garantia(), new Garantia());

        when(repository.findByEmprestimoId(emprestimoId)).thenReturn(garantias);
        when(mapper.toResponseDTO(any(Garantia.class))).thenReturn(new GarantiaResponseDTO());

        // Act
        List<GarantiaResponseDTO> result = useCase.execute(emprestimoId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findByEmprestimoId(emprestimoId);
        verify(mapper, times(2)).toResponseDTO(any(Garantia.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando um empréstimo não tem garantias")
    void deveRetornarListaVaziaParaEmprestimoSemGarantias() {
        // Arrange
        Long emprestimoId = 2L;
        when(repository.findByEmprestimoId(emprestimoId)).thenReturn(Collections.emptyList());

        // Act
        List<GarantiaResponseDTO> result = useCase.execute(emprestimoId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(repository).findByEmprestimoId(emprestimoId);
        verify(mapper, never()).toResponseDTO(any());
    }
}
