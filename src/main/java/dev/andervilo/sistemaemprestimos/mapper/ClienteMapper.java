package dev.andervilo.sistemaemprestimos.mapper;

import dev.andervilo.sistemaemprestimos.domain.entity.Cliente;
import dev.andervilo.sistemaemprestimos.dto.ClienteRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.ClienteResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    Cliente toEntity(ClienteRequestDTO dto);

    ClienteResponseDTO toResponseDTO(Cliente entity);

    void updateEntityFromDTO(ClienteRequestDTO dto, @MappingTarget Cliente entity);
}
