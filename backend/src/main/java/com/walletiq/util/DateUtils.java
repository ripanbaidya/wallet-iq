package com.walletiq.util;

import java.time.LocalDate;
import java.time.YearMonth;

public final class DateUtils {

    public static LocalDate startOfMonth(YearMonth month) {
        return month.atDay(1);
    }

    public static LocalDate endOfMonth(YearMonth month) {
        return month.atEndOfMonth();
    }
}
