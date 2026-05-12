package online.walletiq;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordEncoderTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("Generate B-Crypt encoded password")
    void shouldGenerateEncodedPassword() {
        String rawPassword = "password12345";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Raw Password      : " + rawPassword);
        System.out.println("Encoded Password  : " + encodedPassword);
    }
}