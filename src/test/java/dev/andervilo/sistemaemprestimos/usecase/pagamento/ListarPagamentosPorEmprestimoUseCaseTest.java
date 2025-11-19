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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarPagamentosPorEmprestimoUseCaseTest {

    @Mock private PagamentoRepository repository;
    @Mock private PagamentoMapper mapper;
    @InjectMocks private ListarPagamentosPorEmprestimoUseCase useCase;

    @Test
    @DisplayName("Deve listar os pagamentos de um empréstimo com sucesso")
    void deveListarPagamentosPorEmprestimoComSucesso() {
        // Arrange
        Long emprestimoId = 1L;
        List<Pagamento> pagamentos = List.of(new Pagamento(), new Pagamento());

        when(repository.findByEmprestimoIdOrderByDataPagamentoDesc(emprestimoId)).thenReturn(pagamentos);
        when(mapper.toResponseDTO(any(Pagamento.class))).thenReturn(new PagamentoResponseDTO());

        // Act
        List<PagamentoResponseDTO> result = useCase.execute(emprestimoId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findByEmprestimoIdOrderByDataPagamentoDesc(emprestimoId);
        verify(mapper, times(2)).toResponseDTO(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando um empréstimo não tem pagamentos")
    void deveRetornarListaVaziaParaEmprestimoSemPagamentos() {
        // Arrange
        Long emprestimoId = 2L;
        when(repository.findByEmprestimoIdOrderByDataPagamentoDesc(emprestimoId)).thenReturn(Collections.emptyList());

        // Act
        List<PagamentoResponseDTO> result = useCase.execute(emprestimoId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(repository).findByEmprestimoIdOrderByDataPagamentoDesc(emprestimoId);
        verify(mapper, never()).toResponseDTO(any());
    }
}
