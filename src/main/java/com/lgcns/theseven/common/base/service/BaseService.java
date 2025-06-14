package com.lgcns.theseven.common.base.service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseService<T> {
    List<T> findAll();

    Optional<T> findById(UUID id);

    T save(T entity);

    T update(UUID id, T entity);

    void deleteById(UUID id);
}