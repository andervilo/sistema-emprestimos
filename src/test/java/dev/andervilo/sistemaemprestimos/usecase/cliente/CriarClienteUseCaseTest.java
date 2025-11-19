package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.domain.enums.TipoPessoa;
import dev.andervilo.sistemaemprestimos.dto.ClienteRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.ClienteResponseDTO;
import dev.andervilo.sistemaemprestimos.mapper.ClienteMapper;
import dev.andervilo.sistemaemprestimos.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private CriarClienteUseCase criarClienteUseCase;

    private ClienteRequestDTO requestDTO;
    private Cliente cliente;
    private ClienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("João da Silva");
        requestDTO.setCpfCnpj("123.456.789-00");
        requestDTO.setTipoPessoa(TipoPessoa.FISICA);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João da Silva");
        cliente.setCpfCnpj("123.456.789-00");
        cliente.setTipoPessoa(TipoPessoa.FISICA);

        responseDTO = new ClienteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("João da Silva");
        responseDTO.setCpfCnpj("123.456.789-00");
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso")
    void deveCriarClienteComSucesso() {
        // Arrange
        when(clienteRepository.existsByCpfCnpj(requestDTO.getCpfCnpj())).thenReturn(false);
        when(clienteMapper.toEntity(requestDTO)).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toResponseDTO(cliente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO result = criarClienteUseCase.execute(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getNome(), result.getNome());
        verify(clienteRepository).existsByCpfCnpj(requestDTO.getCpfCnpj());
        verify(clienteMapper).toEntity(requestDTO);
        verify(clienteRepository).save(cliente);
        verify(clienteMapper).toResponseDTO(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF/CNPJ já existe")
    void deveLancarExcecaoQuandoCpfCnpjJaExiste() {
        // Arrange
        when(clienteRepository.existsByCpfCnpj(requestDTO.getCpfCnpj())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            criarClienteUseCase.execute(requestDTO);
        });

        assertEquals("CPF/CNPJ já cadastrado", exception.getMessage());
        verify(clienteRepository).existsByCpfCnpj(requestDTO.getCpfCnpj());
        verify(clienteMapper, never()).toEntity(any());
        verify(clienteRepository, never()).save(any());
        verify(clienteMapper, never()).toResponseDTO(any());
    }
}
