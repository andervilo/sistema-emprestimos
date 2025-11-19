package dev.andervilo.sistemaemprestimos.usecase.garantia;

import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.entity.Garantia;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoGarantia;
import dev.andervilo.sistemaemprestimos.dto.GarantiaRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.GarantiaResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.GarantiaMapper;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import dev.andervilo.sistemaemprestimos.repository.GarantiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarGarantiaUseCaseTest {

    @Mock private GarantiaRepository garantiaRepository;
    @Mock private EmprestimoRepository emprestimoRepository;
    @Mock private GarantiaMapper garantiaMapper;

    @InjectMocks private CriarGarantiaUseCase criarGarantiaUseCase;

    private Emprestimo emprestimo;
    private GarantiaRequestDTO requestDTO;
    private Garantia garantia;
    private GarantiaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        emprestimo = new Emprestimo();
        emprestimo.setId(10L);

        requestDTO = new GarantiaRequestDTO();
        requestDTO.setEmprestimoId(10L);
        requestDTO.setTipoGarantia(TipoGarantia.IMOVEL);
        requestDTO.setDescricao("Apartamento no centro");
        requestDTO.setValorAvaliado(new BigDecimal("250000.00"));

        garantia = new Garantia();
        garantia.setId(20L);

        responseDTO = new GarantiaResponseDTO();
        responseDTO.setId(20L);
        responseDTO.setEmprestimoId(10L);
    }

    @Test
    @DisplayName("Deve criar uma garantia com sucesso")
    void deveCriarGarantiaComSucesso() {
        // Arrange
        when(emprestimoRepository.findById(10L)).thenReturn(Optional.of(emprestimo));
        when(garantiaMapper.toEntity(requestDTO)).thenReturn(garantia);
        when(garantiaRepository.save(any(Garantia.class))).thenReturn(garantia);
        when(garantiaMapper.toResponseDTO(garantia)).thenReturn(responseDTO);

        // Act
        GarantiaResponseDTO result = criarGarantiaUseCase.execute(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(emprestimo, garantia.getEmprestimo());

        verify(emprestimoRepository).findById(10L);
        verify(garantiaRepository).save(garantia);
        verify(garantiaMapper).toEntity(requestDTO);
        verify(garantiaMapper).toResponseDTO(garantia);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar garantia para empréstimo inexistente")
    void deveLancarExcecaoParaEmprestimoInexistente() {
        // Arrange
        when(emprestimoRepository.findById(99L)).thenReturn(Optional.empty());
        requestDTO.setEmprestimoId(99L);

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> criarGarantiaUseCase.execute(requestDTO));
        assertEquals("Empréstimo não encontrado", exception.getMessage());
        verify(emprestimoRepository).findById(99L);
        verify(garantiaRepository, never()).save(any());
    }
}
