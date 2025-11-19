package dev.andervilo.sistemaemprestimos.usecase.emprestimo;

import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.EmprestimoMapper;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
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
class BuscarEmprestimoPorIdUseCaseTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private EmprestimoMapper emprestimoMapper;

    @InjectMocks
    private BuscarEmprestimoPorIdUseCase buscarEmprestimoPorIdUseCase;

    private Emprestimo emprestimo;
    private EmprestimoResponseDTO responseDTO;
    private final Long emprestimoId = 1L;

    @BeforeEach
    void setUp() {
        emprestimo = new Emprestimo();
        emprestimo.setId(emprestimoId);

        responseDTO = new EmprestimoResponseDTO();
        responseDTO.setId(emprestimoId);
    }

    @Test
    @DisplayName("Deve buscar um empréstimo por ID com sucesso")
    void deveBuscarEmprestimoPorIdComSucesso() {
        // Arrange
        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimo));
        when(emprestimoMapper.toResponseDTO(emprestimo)).thenReturn(responseDTO);

        // Act
        EmprestimoResponseDTO result = buscarEmprestimoPorIdUseCase.execute(emprestimoId);

        // Assert
        assertNotNull(result);
        assertEquals(emprestimoId, result.getId());
        verify(emprestimoRepository).findById(emprestimoId);
        verify(emprestimoMapper).toResponseDTO(emprestimo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar empréstimo com ID inexistente")
    void deveLancarExcecaoAoBuscarEmprestimoComIdInexistente() {
        // Arrange
        Long idInexistente = 99L;
        when(emprestimoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            buscarEmprestimoPorIdUseCase.execute(idInexistente);
        });

        assertEquals("Empréstimo não encontrado", exception.getMessage());
        verify(emprestimoRepository).findById(idInexistente);
        verify(emprestimoMapper, never()).toResponseDTO(any());
    }
}
