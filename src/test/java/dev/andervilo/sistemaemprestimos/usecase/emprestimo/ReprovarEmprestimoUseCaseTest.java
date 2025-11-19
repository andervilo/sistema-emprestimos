package dev.andervilo.sistemaemprestimos.usecase.emprestimo;

import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.EmprestimoMapper;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReprovarEmprestimoUseCaseTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private EmprestimoMapper emprestimoMapper;

    @InjectMocks
    private ReprovarEmprestimoUseCase reprovarEmprestimoUseCase;

    private Emprestimo emprestimoPendente;
    private EmprestimoResponseDTO responseDTO;
    private final Long emprestimoId = 1L;

    @BeforeEach
    void setUp() {
        emprestimoPendente = new Emprestimo();
        emprestimoPendente.setId(emprestimoId);
        emprestimoPendente.setStatus(StatusEmprestimo.PENDENTE_ANALISE);

        responseDTO = new EmprestimoResponseDTO();
        responseDTO.setId(emprestimoId);
        responseDTO.setStatus(StatusEmprestimo.REPROVADO);
    }

    @ParameterizedTest
    @EnumSource(value = StatusEmprestimo.class, names = {"PENDENTE_ANALISE", "EM_ANALISE"})
    @DisplayName("Deve reprovar um empréstimo com sucesso")
    void deveReprovarEmprestimoComSucesso(StatusEmprestimo statusInicial) {
        // Arrange
        emprestimoPendente.setStatus(statusInicial);
        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimoPendente));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoPendente);
        when(emprestimoMapper.toResponseDTO(emprestimoPendente)).thenReturn(responseDTO);

        // Act
        EmprestimoResponseDTO result = reprovarEmprestimoUseCase.execute(emprestimoId);

        // Assert
        assertNotNull(result);
        assertEquals(StatusEmprestimo.REPROVADO, result.getStatus());
        assertEquals(StatusEmprestimo.REPROVADO, emprestimoPendente.getStatus());

        verify(emprestimoRepository).findById(emprestimoId);
        verify(emprestimoRepository).save(emprestimoPendente);
        verify(emprestimoMapper).toResponseDTO(emprestimoPendente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar reprovar empréstimo inexistente")
    void deveLancarExcecaoAoReprovarEmprestimoInexistente() {
        // Arrange
        when(emprestimoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reprovarEmprestimoUseCase.execute(99L);
        });

        assertEquals("Empréstimo não encontrado", exception.getMessage());
        verify(emprestimoRepository).findById(99L);
        verify(emprestimoRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = StatusEmprestimo.class, names = {"ATIVO", "REPROVADO", "FINALIZADO", "CANCELADO"})
    @DisplayName("Deve lançar exceção ao reprovar empréstimo com status inválido")
    void deveLancarExcecaoAoReprovarEmprestimoComStatusInvalido(StatusEmprestimo statusInvalido) {
        // Arrange
        emprestimoPendente.setStatus(statusInvalido);
        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimoPendente));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reprovarEmprestimoUseCase.execute(emprestimoId);
        });

        assertEquals("Empréstimo não pode ser reprovado no status atual", exception.getMessage());
        verify(emprestimoRepository).findById(emprestimoId);
        verify(emprestimoRepository, never()).save(any());
    }
}
