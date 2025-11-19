package dev.andervilo.sistemaemprestimos.usecase.emprestimo;

import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.EmprestimoMapper;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
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
class ListarEmprestimosUseCaseTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private EmprestimoMapper emprestimoMapper;

    @InjectMocks
    private ListarEmprestimosUseCase listarEmprestimosUseCase;

    @Test
    @DisplayName("Deve listar todos os empréstimos com sucesso")
    void deveListarTodosOsEmprestimosComSucesso() {
        // Arrange
        Emprestimo emprestimo1 = new Emprestimo();
        Emprestimo emprestimo2 = new Emprestimo();
        List<Emprestimo> emprestimos = List.of(emprestimo1, emprestimo2);

        when(emprestimoRepository.findAll()).thenReturn(emprestimos);
        when(emprestimoMapper.toResponseDTO(any(Emprestimo.class))).thenReturn(new EmprestimoResponseDTO());

        // Act
        List<EmprestimoResponseDTO> result = listarEmprestimosUseCase.execute();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(emprestimoRepository).findAll();
        verify(emprestimoMapper, times(2)).toResponseDTO(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não há empréstimos")
    void deveRetornarListaVaziaQuandoNaoHaEmprestimos() {
        // Arrange
        when(emprestimoRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<EmprestimoResponseDTO> result = listarEmprestimosUseCase.execute();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(emprestimoRepository).findAll();
        verify(emprestimoMapper, never()).toResponseDTO(any());
    }
}
