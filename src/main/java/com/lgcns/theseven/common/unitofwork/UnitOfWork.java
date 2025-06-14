package com.lgcns.theseven.common.unitofwork;

public interface UnitOfWork {
    /**
     * Thực hiện công việc trong 1 transaction, tự động commit hoặc rollback khi có lỗi.
     * @param action hàm cần chạy trong 1 transaction
     * @param <T> kiểu trả về
     * @return kết quả sau khi xử lý
     */
    <T> T execute(UnitOfWorkAction<T> action);
}