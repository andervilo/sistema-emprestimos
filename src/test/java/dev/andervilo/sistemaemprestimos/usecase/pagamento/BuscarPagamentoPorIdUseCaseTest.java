package dev.andervilo.sistemaemprestimos.usecase.pagamento;

import dev.andervilo.sistemaemprestimos.domain.entity.Pagamento;
import dev.andervilo.sistemaemprestimos.dto.PagamentoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.PagamentoMapper;
import dev.andervilo.sistemaemprestimos.repository.PagamentoRepository;
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
class BuscarPagamentoPorIdUseCaseTest {

    @Mock private PagamentoRepository repository;
    @Mock private PagamentoMapper mapper;
    @InjectMocks private BuscarPagamentoPorIdUseCase useCase;

    @Test
    @DisplayName("Deve buscar um pagamento por ID com sucesso")
    void deveBuscarPagamentoPorIdComSucesso() {
        // Arrange
        Long id = 1L;
        Pagamento pagamento = new Pagamento();
        pagamento.setId(id);
        PagamentoResponseDTO responseDTO = new PagamentoResponseDTO();
        responseDTO.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(pagamento));
        when(mapper.toResponseDTO(pagamento)).thenReturn(responseDTO);

        // Act
        PagamentoResponseDTO result = useCase.execute(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(repository).findById(id);
        verify(mapper).toResponseDTO(pagamento);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pagamento com ID inexistente")
    void deveLancarExcecaoParaIdInexistente() {
        // Arrange
        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> useCase.execute(id));
        assertEquals("Pagamento não encontrado", exception.getMessage());
        verify(repository).findById(id);
        verify(mapper, never()).toResponseDTO(any());
    }
}
