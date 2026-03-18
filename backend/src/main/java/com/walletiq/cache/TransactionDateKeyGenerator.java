package com.walletiq.cache;

import com.walletiq.dto.transaction.CreateTransactionRequest;
import com.walletiq.dto.transaction.UpdateTransactionRequest;
import com.walletiq.util.SecurityUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.YearMonth;

/**
 * Generates dashboard eviction key from a transaction request's date.
 * Expects first param to be CreateTransactionRequest or UpdateTransactionRequest.
 * Use for: @CacheEvict on createTransaction / updateTransaction
 */
@Component("transactionDateKeyGenerator")
public class TransactionDateKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String userId = SecurityUtils.getCurrentUserId().toString();

        // createTransaction(CreateTransactionRequest request)
        if (params[0] instanceof CreateTransactionRequest req) {
            return userId + ":" + YearMonth.from(req.date());
        }

        // updateTransaction(UUID id, UpdateTransactionRequest request)
        if (params.length > 1 && params[1] instanceof UpdateTransactionRequest req) {
            return userId + ":" + YearMonth.from(req.date());
        }

        // Fallback — should never hit in practice
        return userId;
    }
}