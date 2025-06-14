package com.lgcns.theseven.common.base.mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @param <DTO>    Data Transfer Object (application layer)
 * @param <DOMAIN> Domain Model (domain layer)
 * @param <ENTITY> JPA Entity (infrastructure layer)
 */
public interface BaseMapper<DTO, DOMAIN, ENTITY> {

    DOMAIN toDomain(ENTITY entity);

    ENTITY toEntity(DOMAIN domain);

    DTO toDto(DOMAIN domain);

    DOMAIN toDomainFromDto(DTO dto);

    default List<DOMAIN> toDomainListFromEntity(List<ENTITY> entities) {
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    default List<ENTITY> toEntityListFromDomain(List<DOMAIN> domains) {
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }

    default List<DTO> toDtoList(List<DOMAIN> domains) {
        return domains.stream().map(this::toDto).collect(Collectors.toList());
    }

    default List<DOMAIN> toDomainListFromDto(List<DTO> dtos) {
        return dtos.stream().map(this::toDomainFromDto).collect(Collectors.toList());
    }
}