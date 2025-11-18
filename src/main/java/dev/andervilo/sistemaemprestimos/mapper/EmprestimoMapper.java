package dev.andervilo.sistemaemprestimos.mapper;

import dev.andervilo.sistemaemprestimos.domain.entity.Emprestimo;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoRequestDTO;
import dev.andervilo.sistemaemprestimos.dto.EmprestimoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmprestimoMapper {

    @Mapping(target = "cliente.id", source = "clienteId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataAprovacao", ignore = true)
    @Mapping(target = "dataVencimentoPrimeiraParcela", ignore = true)
    @Mapping(target = "dataSolicitacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "valorParcela", ignore = true)
    @Mapping(target = "pagamentos", ignore = true)
    @Mapping(target = "garantias", ignore = true)
    Emprestimo toEntity(EmprestimoRequestDTO dto);

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNome", source = "cliente.nome")
    EmprestimoResponseDTO toResponseDTO(Emprestimo entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataAprovacao", ignore = true)
    @Mapping(target = "dataVencimentoPrimeiraParcela", ignore = true)
    @Mapping(target = "dataSolicitacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "valorParcela", ignore = true)
    @Mapping(target = "pagamentos", ignore = true)
    @Mapping(target = "garantias", ignore = true)
    void updateEntityFromDTO(EmprestimoRequestDTO dto, @MappingTarget Emprestimo entity);
}
