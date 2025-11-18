package dev.andervilo.sistemaemprestimos.mapper;

import dev.andervilo.sistemaemprestimos.domain.entity.Pagamento;
import dev.andervilo.sistemaemprestimos.dto.PagamentoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.PagamentoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PagamentoMapper {

    @Mapping(target = "emprestimo.id", source = "emprestimoId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataRegistro", ignore = true)
    Pagamento toEntity(PagamentoRequestDTO dto);

    @Mapping(target = "emprestimoId", source = "emprestimo.id")
    PagamentoResponseDTO toResponseDTO(Pagamento entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emprestimo", ignore = true)
    @Mapping(target = "dataRegistro", ignore = true)
    void updateEntityFromDTO(PagamentoRequestDTO dto, @MappingTarget Pagamento entity);
}
