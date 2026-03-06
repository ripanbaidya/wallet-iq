package com.walletiq.backend.util;

import com.walletiq.backend.enums.ErrorCode;
import com.walletiq.backend.exception.KeyLoadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public final class KeyUtils {

    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";
    private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";
    private static final String KEY_ALGORITHM = "RSA";

    private KeyUtils() {
    }

    /**
     * Loads an RSA private key from the given classpath resource path.
     */
    public static PrivateKey loadPrivateKey(String pemPath, ResourceLoader resourceLoader) {
        log.info("Loading private key from {}", pemPath);

        try {
            byte[] keyBytes = loadKeyBytes(pemPath, resourceLoader, PRIVATE_KEY_HEADER, PRIVATE_KEY_FOOTER);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey privateKey = KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(spec);

            log.info("Private key loaded successfully from {}", pemPath);

            return privateKey;

        } catch (KeyLoadException ex) {
            throw ex;
        } catch (Exception ex) {
            String msg = "Failed to load private key from: %s" + pemPath;
            throw new KeyLoadException(ErrorCode.PRIVATE_KEY_LOAD_FAILED, msg, ex);
        }
    }

    /**
     * Loads an RSA public key from the given classpath resource path.
     */
    public static PublicKey loadPublicKey(String pemPath, ResourceLoader resourceLoader) {
        log.info("Loading public key from {}", pemPath);

        try {
            byte[] keyBytes = loadKeyBytes(pemPath, resourceLoader, PUBLIC_KEY_HEADER, PUBLIC_KEY_FOOTER);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            PublicKey publicKey = KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(spec);

            log.info("Public key loaded successfully from {}", pemPath);

            return publicKey;

        } catch (KeyLoadException ex) {
            throw ex;
        } catch (Exception ex) {
            String msg = "Failed to load public key from: %s" + pemPath;
            throw new KeyLoadException(ErrorCode.PUBLIC_KEY_LOAD_FAILED, msg, ex);
        }
    }

    /**
     * Reads the PEM file, strips headers, and decodes Base64.
     */
    private static byte[] loadKeyBytes(String pemPath, ResourceLoader resourceLoader,
                                       String header, String footer) {

        String keyContent = readKeyFromResource(pemPath, resourceLoader);
        String cleanedKey = stripPem(keyContent, header, footer);

        try {
            return Base64.getDecoder().decode(cleanedKey);
        } catch (IllegalArgumentException ex) {
            throw new KeyLoadException(ErrorCode.INVALID_KEY_FORMAT,
                "Invalid Base64 encoding in key file: " + pemPath,
                ex
            );
        }
    }

    /**
     * Reads raw key content from a resource.
     */
    private static String readKeyFromResource(String location, ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource(location);

        if (!resource.exists()) {
            throw new KeyLoadException(ErrorCode.KEY_FILE_NOT_FOUND, "Key file not found: " + location);
        }
        if (!resource.isReadable()) {
            throw new KeyLoadException(ErrorCode.KEY_FILE_NOT_READABLE, "Key file is not readable: " + location);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new KeyLoadException(ErrorCode.KEY_FILE_READ_FAILED, "Failed to read key file: " + location, ex);
        }
    }

    /**
     * Removes PEM header/footer and whitespace and leave only raw base64
     * encoded key content
     */
    private static String stripPem(String content, String header, String footer) {
        return content.replace(header, "").replace(footer, "")
            .replaceAll("\\s", "");
    }
}