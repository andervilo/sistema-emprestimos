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
class ListarEmprestimosPorClienteUseCaseTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private EmprestimoMapper emprestimoMapper;

    @InjectMocks
    private ListarEmprestimosPorClienteUseCase listarEmprestimosPorClienteUseCase;

    @Test
    @DisplayName("Deve listar os empréstimos de um cliente com sucesso")
    void deveListarEmprestimosDeUmClienteComSucesso() {
        // Arrange
        Long clienteId = 1L;
        Emprestimo emprestimo1 = new Emprestimo();
        Emprestimo emprestimo2 = new Emprestimo();
        List<Emprestimo> emprestimos = List.of(emprestimo1, emprestimo2);

        when(emprestimoRepository.findByClienteId(clienteId)).thenReturn(emprestimos);
        when(emprestimoMapper.toResponseDTO(any(Emprestimo.class))).thenReturn(new EmprestimoResponseDTO());

        // Act
        List<EmprestimoResponseDTO> result = listarEmprestimosPorClienteUseCase.execute(clienteId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(emprestimoRepository).findByClienteId(clienteId);
        verify(emprestimoMapper, times(2)).toResponseDTO(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando um cliente não tem empréstimos")
    void deveRetornarListaVaziaQuandoClienteNaoTemEmprestimos() {
        // Arrange
        Long clienteId = 2L;
        when(emprestimoRepository.findByClienteId(clienteId)).thenReturn(Collections.emptyList());

        // Act
        List<EmprestimoResponseDTO> result = listarEmprestimosPorClienteUseCase.execute(clienteId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(emprestimoRepository).findByClienteId(clienteId);
        verify(emprestimoMapper, never()).toResponseDTO(any());
    }
}
