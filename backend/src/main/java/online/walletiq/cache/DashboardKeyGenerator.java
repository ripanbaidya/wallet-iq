package online.walletiq.cache;

import online.walletiq.util.SecurityUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.YearMonth;

/**
 * Generates cache key: {userId}:{month}
 * Expects first method param to be YearMonth.
 * Use for: dashboard
 */
@Component("dashboardKeyGenerator")
public class DashboardKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        YearMonth month = (YearMonth) params[0]; // getDashboard(YearMonth month)
        return SecurityUtils.getCurrentUserId().toString() + ":" + month.toString();
    }
}