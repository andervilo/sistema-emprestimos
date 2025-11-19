package dev.andervilo.sistemaemprestimos.usecase.cliente;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private AtualizarClienteUseCase atualizarClienteUseCase;

    private Cliente clienteExistente;
    private ClienteRequestDTO requestDTO;
    private ClienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        clienteExistente = new Cliente();
        clienteExistente.setId(1L);
        clienteExistente.setNome("João da Silva");
        clienteExistente.setCpfCnpj("123.456.789-00");

        requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("João da Silva Santos");
        requestDTO.setCpfCnpj("111.222.333-44");
        requestDTO.setEmail("joao.santos@example.com");

        responseDTO = new ClienteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNome("João da Silva Santos");
        responseDTO.setCpfCnpj("111.222.333-44");
        responseDTO.setEmail("joao.santos@example.com");
    }

    @Test
    @DisplayName("Deve atualizar um cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.existsByCpfCnpj(requestDTO.getCpfCnpj())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);
        when(clienteMapper.toResponseDTO(clienteExistente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO result = atualizarClienteUseCase.execute(1L, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.getNome(), result.getNome());
        assertEquals(responseDTO.getCpfCnpj(), result.getCpfCnpj());
        verify(clienteRepository).findById(1L);
        verify(clienteRepository).existsByCpfCnpj(requestDTO.getCpfCnpj());
        verify(clienteMapper).updateEntityFromDTO(requestDTO, clienteExistente);
        verify(clienteRepository).save(clienteExistente);
        verify(clienteMapper).toResponseDTO(clienteExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar cliente inexistente")
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        // Arrange
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            atualizarClienteUseCase.execute(99L, requestDTO);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteRepository).findById(99L);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao usar CPF/CNPJ de outro cliente")
    void deveLancarExcecaoAoUsarCpfCnpjDeOutroCliente() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.existsByCpfCnpj(requestDTO.getCpfCnpj())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            atualizarClienteUseCase.execute(1L, requestDTO);
        });

        assertEquals("CPF/CNPJ já cadastrado para outro cliente", exception.getMessage());
        verify(clienteRepository).findById(1L);
        verify(clienteRepository).existsByCpfCnpj(requestDTO.getCpfCnpj());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve permitir atualização sem alterar CPF/CNPJ")
    void devePermitirAtualizacaoSemAlterarCpfCnpj() {
        // Arrange
        requestDTO.setCpfCnpj(clienteExistente.getCpfCnpj()); // Mesmo CPF/CNPJ
        responseDTO.setCpfCnpj(clienteExistente.getCpfCnpj());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);
        when(clienteMapper.toResponseDTO(clienteExistente)).thenReturn(responseDTO);

        // Act
        ClienteResponseDTO result = atualizarClienteUseCase.execute(1L, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(clienteExistente.getCpfCnpj(), result.getCpfCnpj());
        verify(clienteRepository).findById(1L);
        verify(clienteRepository, never()).existsByCpfCnpj(anyString()); // Não deve chamar a verificação de existência
        verify(clienteMapper).updateEntityFromDTO(requestDTO, clienteExistente);
        verify(clienteRepository).save(clienteExistente);
        verify(clienteMapper).toResponseDTO(clienteExistente);
    }
}
