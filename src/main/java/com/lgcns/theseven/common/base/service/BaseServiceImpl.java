package com.lgcns.theseven.common.base.service;


import com.lgcns.theseven.common.base.repository.BaseRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<T> implements BaseService<T> {

    protected final BaseRepository<T, UUID> repository;

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<T> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public T update(UUID id, T entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}