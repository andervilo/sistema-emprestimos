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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AprovarEmprestimoUseCaseTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private EmprestimoMapper emprestimoMapper;

    @InjectMocks
    private AprovarEmprestimoUseCase aprovarEmprestimoUseCase;

    private Emprestimo emprestimoPendente;
    private EmprestimoResponseDTO responseDTO;
    private final Long emprestimoId = 1L;
    private final LocalDate dataVencimento = LocalDate.now().plusMonths(1);

    @BeforeEach
    void setUp() {
        emprestimoPendente = new Emprestimo();
        emprestimoPendente.setId(emprestimoId);
        emprestimoPendente.setStatus(StatusEmprestimo.PENDENTE_ANALISE);

        responseDTO = new EmprestimoResponseDTO();
        responseDTO.setId(emprestimoId);
        responseDTO.setStatus(StatusEmprestimo.ATIVO);
    }

    @ParameterizedTest
    @EnumSource(value = StatusEmprestimo.class, names = {"PENDENTE_ANALISE", "EM_ANALISE"})
    @DisplayName("Deve aprovar um empréstimo com sucesso")
    void deveAprovarEmprestimoComSucesso(StatusEmprestimo statusInicial) {
        // Arrange
        emprestimoPendente.setStatus(statusInicial);
        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimoPendente));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoPendente);
        when(emprestimoMapper.toResponseDTO(emprestimoPendente)).thenReturn(responseDTO);

        // Act
        EmprestimoResponseDTO result = aprovarEmprestimoUseCase.execute(emprestimoId, dataVencimento);

        // Assert
        assertNotNull(result);
        assertEquals(StatusEmprestimo.ATIVO, result.getStatus());
        assertEquals(StatusEmprestimo.ATIVO, emprestimoPendente.getStatus());
        assertEquals(LocalDate.now(), emprestimoPendente.getDataAprovacao());
        assertEquals(dataVencimento, emprestimoPendente.getDataVencimentoPrimeiraParcela());

        verify(emprestimoRepository).findById(emprestimoId);
        verify(emprestimoRepository).save(emprestimoPendente);
        verify(emprestimoMapper).toResponseDTO(emprestimoPendente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar aprovar empréstimo inexistente")
    void deveLancarExcecaoAoAprovarEmprestimoInexistente() {
        // Arrange
        when(emprestimoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            aprovarEmprestimoUseCase.execute(99L, dataVencimento);
        });

        assertEquals("Empréstimo não encontrado", exception.getMessage());
        verify(emprestimoRepository).findById(99L);
        verify(emprestimoRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = StatusEmprestimo.class, names = {"ATIVO", "REPROVADO", "FINALIZADO", "CANCELADO"})
    @DisplayName("Deve lançar exceção ao aprovar empréstimo com status inválido")
    void deveLancarExcecaoAoAprovarEmprestimoComStatusInvalido(StatusEmprestimo statusInvalido) {
        // Arrange
        emprestimoPendente.setStatus(statusInvalido);
        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimoPendente));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            aprovarEmprestimoUseCase.execute(emprestimoId, dataVencimento);
        });

        assertEquals("Empréstimo não pode ser aprovado no status atual", exception.getMessage());
        verify(emprestimoRepository).findById(emprestimoId);
        verify(emprestimoRepository, never()).save(any());
    }
}
