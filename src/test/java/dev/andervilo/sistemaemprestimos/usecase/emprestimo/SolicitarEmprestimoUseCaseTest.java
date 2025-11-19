package dev.andervilo.sistemaemprestimos.usecase.emprestimo;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.EmprestimoMapper;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import dev.andervilo.sistemaemprestimos.repository.EmprestimoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitarEmprestimoUseCaseTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EmprestimoMapper emprestimoMapper;

    @InjectMocks
    private SolicitarEmprestimoUseCase solicitarEmprestimoUseCase;

    private EmprestimoRequestDTO requestDTO;
    private Cliente cliente;
    private Emprestimo emprestimo;
    private EmprestimoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João da Silva");

        requestDTO = new EmprestimoRequestDTO();
        requestDTO.setClienteId(1L);
        requestDTO.setValorEmprestimo(new BigDecimal("10000.00"));
        requestDTO.setTaxaJuros(new BigDecimal("2.5"));
        requestDTO.setNumeroParcelas(24);
        requestDTO.setFinalidade("Comprar um carro");

        emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setCliente(cliente);
        emprestimo.setValorEmprestimo(requestDTO.getValorEmprestimo());
        emprestimo.setTaxaJuros(requestDTO.getTaxaJuros());
        emprestimo.setNumeroParcelas(requestDTO.getNumeroParcelas());

        responseDTO = new EmprestimoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setClienteId(1L);
        responseDTO.setStatus(StatusEmprestimo.PENDENTE_ANALISE);
    }

    @Test
    @DisplayName("Deve solicitar um empréstimo com sucesso e calcular a parcela corretamente")
    void deveSolicitarEmprestimoComSucesso() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(emprestimoMapper.toEntity(requestDTO)).thenReturn(emprestimo);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(emprestimoMapper.toResponseDTO(emprestimo)).thenReturn(responseDTO);

        // Act
        EmprestimoResponseDTO result = solicitarEmprestimoUseCase.execute(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(StatusEmprestimo.PENDENTE_ANALISE, result.getStatus());

        ArgumentCaptor<Emprestimo> emprestimoCaptor = ArgumentCaptor.forClass(Emprestimo.class);
        verify(emprestimoRepository).save(emprestimoCaptor.capture());
        Emprestimo savedEmprestimo = emprestimoCaptor.getValue();

        assertEquals(cliente, savedEmprestimo.getCliente());
        assertEquals(StatusEmprestimo.PENDENTE_ANALISE, savedEmprestimo.getStatus());

        // Verifica se o cálculo da parcela está correto
        // PMT = [10000 * 0.025 * (1.025)^24] / [(1.025)^24 - 1] ~= 558.76
        BigDecimal valorParcelaEsperado = new BigDecimal("558.76");
        assertNotNull(savedEmprestimo.getValorParcela());
        assertEquals(0, valorParcelaEsperado.compareTo(savedEmprestimo.getValorParcela()));

        verify(clienteRepository).findById(1L);
        verify(emprestimoMapper).toEntity(requestDTO);
        verify(emprestimoMapper).toResponseDTO(emprestimo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao solicitar empréstimo para cliente inexistente")
    void deveLancarExcecaoAoTentarSolicitarParaClienteInexistente() {
        // Arrange
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        requestDTO.setClienteId(99L);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            solicitarEmprestimoUseCase.execute(requestDTO);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteRepository).findById(99L);
        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve calcular a parcela corretamente com taxa de juros zero")
    void deveCalcularParcelaComTaxaJurosZero() {
        // Arrange
        requestDTO.setTaxaJuros(BigDecimal.ZERO);
        emprestimo.setTaxaJuros(BigDecimal.ZERO);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(emprestimoMapper.toEntity(requestDTO)).thenReturn(emprestimo);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(emprestimoMapper.toResponseDTO(emprestimo)).thenReturn(responseDTO);

        // Act
        solicitarEmprestimoUseCase.execute(requestDTO);

        // Assert
        ArgumentCaptor<Emprestimo> emprestimoCaptor = ArgumentCaptor.forClass(Emprestimo.class);
        verify(emprestimoRepository).save(emprestimoCaptor.capture());
        Emprestimo savedEmprestimo = emprestimoCaptor.getValue();

        // 10000 / 24 = 416.67
        BigDecimal valorParcelaEsperado = new BigDecimal("416.67");
        assertNotNull(savedEmprestimo.getValorParcela());
        assertEquals(0, valorParcelaEsperado.compareTo(savedEmprestimo.getValorParcela()));
    }
}
