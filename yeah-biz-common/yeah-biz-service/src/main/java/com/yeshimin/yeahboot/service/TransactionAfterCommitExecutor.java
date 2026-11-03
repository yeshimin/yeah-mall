package com.yeshimin.yeahboot.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 数据库事务提交后执行外部副作用
 */
@Component
public class TransactionAfterCommitExecutor {

    /**
     * 当前存在事务时延迟到提交后执行，否则立即执行。
     */
    public void execute(Runnable action) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}
