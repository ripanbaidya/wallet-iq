package online.walletiq.cache;

import online.walletiq.util.SecurityUtils;
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
        String userId = SecurityUtils.getCurrentUserId().toString();
        if (params.length == 0){
            return userId;
        }

        StringBuilder key = new StringBuilder(userId);
        for (Object param : params) {
            key.append("-").append(param.toString());
        }

        return key.toString();
    }
}
