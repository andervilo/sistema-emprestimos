package dev.andervilo.sistemaemprestimos.mapper;

import dev.andervilo.sistemaemprestimos.domain.entity.AnaliseCredito;
import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.AnaliseCreditoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AnaliseCreditoMapper {

    @Mapping(target = "cliente.id", source = "clienteId")
    @Mapping(target = "emprestimo.id", source = "emprestimoId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataAnalise", ignore = true)
    AnaliseCredito toEntity(AnaliseCreditoRequestDTO dto);

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNome", source = "cliente.nome")
    @Mapping(target = "emprestimoId", source = "emprestimo.id")
    AnaliseCreditoResponseDTO toResponseDTO(AnaliseCredito entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "emprestimo", ignore = true)
    @Mapping(target = "dataAnalise", ignore = true)
    void updateEntityFromDTO(AnaliseCreditoRequestDTO dto, @MappingTarget AnaliseCredito entity);
}
