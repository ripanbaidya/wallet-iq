package com.walletiq.cache;

import com.walletiq.util.SecurityUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Generates cache key: {userId}
 * Use for: categories, payment-modes
 */
@Component("userKeyGenerator")
public class UserKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return SecurityUtils.getCurrentUserId().toString();
    }
}
