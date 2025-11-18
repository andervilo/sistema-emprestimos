package dev.andervilo.sistemaemprestimos.mapper;

import dev.andervilo.sistemaemprestimos.domain.entity.Garantia;
import dev.andervilo.sistemaemprestimos.dto.GarantiaRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.GarantiaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GarantiaMapper {

    @Mapping(target = "emprestimo.id", source = "emprestimoId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    Garantia toEntity(GarantiaRequestDTO dto);

    @Mapping(target = "emprestimoId", source = "emprestimo.id")
    GarantiaResponseDTO toResponseDTO(Garantia entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emprestimo", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    void updateEntityFromDTO(GarantiaRequestDTO dto, @MappingTarget Garantia entity);
}
