package com.lgcns.theseven.common.unitofwork;


@FunctionalInterface
public interface UnitOfWorkAction<T> {
    T apply();
}