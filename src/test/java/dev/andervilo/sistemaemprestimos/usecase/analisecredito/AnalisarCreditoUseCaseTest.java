package dev.andervilo.sistemaemprestimos.usecase.analisecredito;

import dev.andervilo.sistemaemprestimos.domain.entity.AnaliseCredito;
import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.domain.enums.RecomendacaoAnalise;
import dev.andervilo.sistemaemprestimos.domain.enums.StatusEmprestimo;
import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.AnaliseCreditoMapper;
import dev.andervilo.sistemaemprestimos.repository.AnaliseCreditoRepository;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
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
class AnalisarCreditoUseCaseTest {

    @Mock private AnaliseCreditoRepository analiseCreditoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private EmprestimoRepository emprestimoRepository;
    @Mock private AnaliseCreditoMapper analiseCreditoMapper;

    @InjectMocks private AnalisarCreditoUseCase analisarCreditoUseCase;

    private Cliente cliente;
    private Emprestimo emprestimo;
    private AnaliseCreditoRequestDTO requestDTO;
    private AnaliseCredito analiseCredito;
    private AnaliseCreditoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);

        emprestimo = new Emprestimo();
        emprestimo.setId(10L);
        emprestimo.setStatus(StatusEmprestimo.PENDENTE_ANALISE);

        requestDTO = new AnaliseCreditoRequestDTO();
        requestDTO.setClienteId(1L);
        requestDTO.setEmprestimoId(10L);
        requestDTO.setScoreCredito(750);
        requestDTO.setRecomendacao(RecomendacaoAnalise.APROVADO);

        analiseCredito = new AnaliseCredito();
        analiseCredito.setId(100L);

        responseDTO = new AnaliseCreditoResponseDTO();
        responseDTO.setId(100L);
    }

    @Test
    @DisplayName("Deve analisar crédito para um empréstimo e atualizar seu status")
    void deveAnalisarCreditoParaEmprestimoEAtualizarStatus() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(emprestimoRepository.findById(10L)).thenReturn(Optional.of(emprestimo));
        when(analiseCreditoMapper.toEntity(requestDTO)).thenReturn(analiseCredito);
        when(analiseCreditoRepository.save(any(AnaliseCredito.class))).thenReturn(analiseCredito);
        when(analiseCreditoMapper.toResponseDTO(analiseCredito)).thenReturn(responseDTO);

        // Act
        AnalisarCreditoResponseDTO result = analisarCreditoUseCase.execute(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(StatusEmprestimo.EM_ANALISE, emprestimo.getStatus());
        assertEquals(cliente, analiseCredito.getCliente());
        assertEquals(emprestimo, analiseCredito.getEmprestimo());

        verify(clienteRepository).findById(1L);
        verify(emprestimoRepository).findById(10L);
        verify(emprestimoRepository).save(emprestimo);
        verify(analiseCreditoRepository).save(analiseCredito);
    }

    @Test
    @DisplayName("Deve analisar crédito para um cliente sem empréstimo específico")
    void deveAnalisarCreditoSemEmprestimo() {
        // Arrange
        requestDTO.setEmprestimoId(null);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(analiseCreditoMapper.toEntity(requestDTO)).thenReturn(analiseCredito);
        when(analiseCreditoRepository.save(any(AnaliseCredito.class))).thenReturn(analiseCredito);
        when(analiseCreditoMapper.toResponseDTO(analiseCredito)).thenReturn(responseDTO);

        // Act
        AnalisarCreditoResponseDTO result = analisarCreditoUseCase.execute(requestDTO);

        // Assert
        assertNotNull(result);
        assertNull(analiseCredito.getEmprestimo());
        verify(emprestimoRepository, never()).findById(any());
        verify(emprestimoRepository, never()).save(any());
        verify(analiseCreditoRepository).save(analiseCredito);
    }

    @Test
    @DisplayName("Deve lançar exceção ao analisar crédito para cliente inexistente")
    void deveLancarExcecaoParaClienteInexistente() {
        // Arrange
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        requestDTO.setClienteId(99L);

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> analisarCreditoUseCase.execute(requestDTO));
        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao analisar crédito para empréstimo inexistente")
    void deveLancarExcecaoParaEmprestimoInexistente() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(emprestimoRepository.findById(99L)).thenReturn(Optional.empty());
        requestDTO.setEmprestimoId(99L);

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> analisarCreditoUseCase.execute(requestDTO));
        assertEquals("Empréstimo não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve alterar status de empréstimo que não está pendente")
    void naoDeveAlterarStatusDeEmprestimoNaoPendente() {
        // Arrange
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(emprestimoRepository.findById(10L)).thenReturn(Optional.of(emprestimo));
        when(analiseCreditoMapper.toEntity(requestDTO)).thenReturn(analiseCredito);
        when(analiseCreditoRepository.save(any(AnaliseCredito.class))).thenReturn(analiseCredito);
        when(analiseCreditoMapper.toResponseDTO(analiseCredito)).thenReturn(responseDTO);

        // Act
        analisarCreditoUseCase.execute(requestDTO);

        // Assert
        assertEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus());
        verify(emprestimoRepository, never()).save(emprestimo);
        verify(analiseCreditoRepository).save(analiseCredito);
    }
}
