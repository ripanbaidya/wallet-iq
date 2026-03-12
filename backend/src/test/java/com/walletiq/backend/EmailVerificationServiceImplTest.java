package com.walletiq.backend;


import com.walletiq.backend.service.impl.EmailVerificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class EmailVerificationServiceImplTest {


    private EmailVerificationServiceImpl service;
    private Method method;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        service = new EmailVerificationServiceImpl(null, null,
            null, null);
        // Access private method
        method = EmailVerificationServiceImpl.class.getDeclaredMethod("generateOtp");

        method.setAccessible(true);
    }

    @Test
    void shouldGenerateValidSixDigitOtp() throws Exception {
        String otp = (String) method.invoke(service);

        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"));

        System.out.println("Generated Otp: " + otp);

        int otpValue = Integer.parseInt(otp);
        assertTrue(otpValue >= 100000 && otpValue <= 999999);
    }

    @Test
    void shouldGenerateDifferentOtps() throws Exception {

        String otp1 = (String) method.invoke(service);
        String otp2 = (String) method.invoke(service);

        assertNotNull(otp1);
        assertNotNull(otp2);

        // Not guaranteed but highly likely
        assertNotEquals(otp1, otp2);
    }

    @Test
    void generateMultipleOtps() throws Exception {
        for (int i = 0; i < 5; i++) {
            String otp = (String) method.invoke(service);
            System.out.println(otp);
            assertEquals(6, otp.length());
        }
    }
}
