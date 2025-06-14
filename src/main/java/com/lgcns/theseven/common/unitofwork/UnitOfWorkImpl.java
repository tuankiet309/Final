package com.lgcns.theseven.common.unitofwork;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@RequiredArgsConstructor
public class UnitOfWorkImpl implements UnitOfWork {

    private final PlatformTransactionManager transactionManager;

    @Override
    public <T> T execute(UnitOfWorkAction<T> action) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        return template.execute(status -> {
            try {
                return action.apply();  // chạy code của bạn trong transaction
            } catch (RuntimeException ex) {
                status.setRollbackOnly(); // rollback khi gặp lỗi
                throw ex;
            }
        });
    }
}