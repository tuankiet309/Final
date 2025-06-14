package com.lgcns.theseven.common.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.io.Serializable;
import java.util.UUID;

public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
}