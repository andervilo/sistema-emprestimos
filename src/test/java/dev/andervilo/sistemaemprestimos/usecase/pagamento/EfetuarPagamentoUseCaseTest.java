package dev.andervilo.sistemaemprestimos.usecase.pagamento;

import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.entity.Pagamento;
import dev.andervilo.sistemaemprestimos.domain.enums.FormaPagamento;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.dto.PagamentoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.PagamentoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.PagamentoMapper;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import dev.andervilo.sistemaemprestimos.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EfetuarPagamentoUseCaseTest {

    @Mock private PagamentoRepository pagamentoRepository;
    @Mock private EmprestimoRepository emprestimoRepository;
    @Mock private PagamentoMapper pagamentoMapper;

    @InjectMocks private EfetuarPagamentoUseCase efetuarPagamentoUseCase;

    private Emprestimo emprestimoAtivo;
    private PagamentoRequestDTO requestDTO;
    private Pagamento pagamento;
    private PagamentoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        emprestimoAtivo = new Emprestimo();
        emprestimoAtivo.setId(10L);
        emprestimoAtivo.setStatus(StatusEmprestimo.ATIVO);
        emprestimoAtivo.setValorParcela(new BigDecimal("500.00"));
        emprestimoAtivo.setNumeroParcelas(12);

        requestDTO = new PagamentoRequestDTO();
        requestDTO.setEmprestimoId(10L);
        requestDTO.setValorPago(new BigDecimal("500.00"));
        requestDTO.setDataPagamento(LocalDate.now());
        requestDTO.setFormaPagamento(FormaPagamento.PIX);

        pagamento = new Pagamento();
        pagamento.setId(30L);

        responseDTO = new PagamentoResponseDTO();
        responseDTO.setId(30L);
    }

    @ParameterizedTest
    @EnumSource(value = StatusEmprestimo.class, names = {"ATIVO", "ATRASADO"})
    @DisplayName("Deve efetuar um pagamento com sucesso para empréstimo com status válido")
    void deveEfetuarPagamentoComSucesso(StatusEmprestimo statusValido) {
        // Arrange
        emprestimoAtivo.setStatus(statusValido);
        when(emprestimoRepository.findById(10L)).thenReturn(Optional.of(emprestimoAtivo));
        when(pagamentoMapper.toEntity(requestDTO)).thenReturn(pagamento);
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);
        when(pagamentoRepository.sumValorPagoByEmprestimoId(10L)).thenReturn(new BigDecimal("500.00"));
        when(pagamentoMapper.toResponseDTO(pagamento)).thenReturn(responseDTO);

        // Act
        PagamentoResponseDTO result = efetuarPagamentoUseCase.execute(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(pagamento.getId(), result.getId());
        assertEquals(emprestimoAtivo, pagamento.getEmprestimo());
        // O status não deve mudar pois o valor total não foi atingido
        assertEquals(statusValido, emprestimoAtivo.getStatus());

        verify(emprestimoRepository).findById(10L);
        verify(pagamentoRepository).save(pagamento);
        verify(pagamentoRepository).sumValorPagoByEmprestimoId(10L);
        verify(emprestimoRepository, never()).save(emprestimoAtivo); // Não deve salvar o empréstimo
    }

    @Test
    @DisplayName("Deve alterar o status do empréstimo para PAGO ao quitar o valor total")
    void deveAlterarStatusParaPagoAoQuitarEmprestimo() {
        // Arrange
        BigDecimal totalEmprestimo = emprestimoAtivo.getValorParcela().multiply(BigDecimal.valueOf(emprestimoAtivo.getNumeroParcelas())); // 6000
        requestDTO.setValorPago(totalEmprestimo);

        when(emprestimoRepository.findById(10L)).thenReturn(Optional.of(emprestimoAtivo));
        when(pagamentoMapper.toEntity(requestDTO)).thenReturn(pagamento);
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);
        when(pagamentoRepository.sumValorPagoByEmprestimoId(10L)).thenReturn(totalEmprestimo);
        when(pagamentoMapper.toResponseDTO(pagamento)).thenReturn(responseDTO);

        // Act
        efetuarPagamentoUseCase.execute(requestDTO);

        // Assert
        assertEquals(StatusEmprestimo.PAGO, emprestimoAtivo.getStatus());
        verify(emprestimoRepository).save(emprestimoAtivo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pagar empréstimo inexistente")
    void deveLancarExcecaoParaEmprestimoInexistente() {
        // Arrange
        when(emprestimoRepository.findById(99L)).thenReturn(Optional.empty());
        requestDTO.setEmprestimoId(99L);

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> efetuarPagamentoUseCase.execute(requestDTO));
        assertEquals("Empréstimo não encontrado", exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = StatusEmprestimo.class, names = {"PENDENTE_ANALISE", "EM_ANALISE", "REPROVADO", "PAGO", "FINALIZADO", "CANCELADO"})
    @DisplayName("Deve lançar exceção ao pagar empréstimo com status inválido")
    void deveLancarExcecaoParaStatusInvalido(StatusEmprestimo statusInvalido) {
        // Arrange
        emprestimoAtivo.setStatus(statusInvalido);
        when(emprestimoRepository.findById(10L)).thenReturn(Optional.of(emprestimoAtivo));

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, () -> efetuarPagamentoUseCase.execute(requestDTO));
        assertEquals("Empréstimo não está em situação que permite pagamentos", exception.getMessage());
    }
}
