package com.walletiq.util;

import com.walletiq.enums.ErrorCode;
import com.walletiq.exception.KeyLoadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Utility class for loading RSA keys from PEM-formatted classpath resources.
 * All paths must use the {@code classpath:} prefix to prevent path traversal.
 * This class is non-instantiable.
 */
@Slf4j
public final class KeyUtils {

    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";
    private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";
    private static final String KEY_ALGORITHM = "RSA";
    private static final String CLASSPATH_PREFIX = "classpath:";

    private KeyUtils() {
    }

    /**
     * Loads an RSA private key (PKCS#8) from a classpath PEM resource.
     *
     * @param pemPath        classpath resource path, e.g. {@code classpath:keys/private.pem}
     * @param resourceLoader Spring resource loader
     * @return the loaded {@link PrivateKey}
     * @throws KeyLoadException if the path is invalid, the file is unreadable,
     *                          or the key cannot be parsed
     */
    public static PrivateKey loadPrivateKey(String pemPath, ResourceLoader resourceLoader) {
        log.info("Loading private key from {}", pemPath);
        validateClasspathLocation(pemPath);

        try {
            byte[] keyBytes = loadKeyBytes(pemPath, resourceLoader, PRIVATE_KEY_HEADER, PRIVATE_KEY_FOOTER);
            PrivateKey privateKey = KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));

            log.info("Private key loaded successfully from {}", pemPath);
            return privateKey;

        } catch (GeneralSecurityException ex) {
            throw new KeyLoadException(ErrorCode.PRIVATE_KEY_LOAD_FAILED,
                String.format("Failed to load private key from: %s", pemPath), ex);
        }
    }

    /**
     * Loads an RSA public key (X.509) from a classpath PEM resource.
     *
     * @param pemPath        classpath resource path, e.g. {@code classpath:keys/public.pem}
     * @param resourceLoader Spring resource loader
     * @return the loaded {@link PublicKey}
     * @throws KeyLoadException if the path is invalid, the file is unreadable,
     *                          or the key cannot be parsed
     */
    public static PublicKey loadPublicKey(String pemPath, ResourceLoader resourceLoader) {
        log.info("Loading public key from {}", pemPath);
        validateClasspathLocation(pemPath);

        try {
            byte[] keyBytes = loadKeyBytes(pemPath, resourceLoader, PUBLIC_KEY_HEADER, PUBLIC_KEY_FOOTER);
            PublicKey publicKey = KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(keyBytes));

            log.info("Public key loaded successfully from {}", pemPath);
            return publicKey;

        } catch (GeneralSecurityException ex) {
            throw new KeyLoadException(ErrorCode.PUBLIC_KEY_LOAD_FAILED,
                String.format("Failed to load public key from: %s", pemPath), ex);
        }
    }

    // Private helpers

    /**
     * Reads the PEM resource, strips the header/footer, and Base64-decodes the content.
     */
    private static byte[] loadKeyBytes(String pemPath, ResourceLoader resourceLoader,
                                       String header, String footer) {
        String raw = readKeyFromResource(pemPath, resourceLoader);
        String cleaned = stripPem(raw, header, footer);

        try {
            return Base64.getDecoder().decode(cleaned);
        } catch (IllegalArgumentException ex) {
            throw new KeyLoadException(ErrorCode.INVALID_KEY_FORMAT,
                "Invalid Base64 encoding in key file: " + pemPath, ex);
        }
    }

    /**
     * Reads the raw text content of a Spring resource.
     *
     * @throws KeyLoadException if the resource does not exist, is not readable,
     *                          or an I/O error occurs
     */
    private static String readKeyFromResource(String location, ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource(location);

        if (!resource.exists()) {
            throw new KeyLoadException(ErrorCode.KEY_FILE_NOT_FOUND,
                "Key file not found: " + location);
        }
        if (!resource.isReadable()) {
            throw new KeyLoadException(ErrorCode.KEY_FILE_NOT_READABLE,
                "Key file is not readable: " + location);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new KeyLoadException(ErrorCode.KEY_FILE_READ_FAILED,
                "Failed to read key file: " + location, ex);
        }
    }

    /**
     * Strips the PEM header, footer, and all whitespace, leaving only the
     * raw Base64-encoded key content.
     */
    private static String stripPem(String content, String header, String footer) {
        return content.replace(header, "")
            .replace(footer, "")
            .replaceAll("\\s", "");
    }

    /**
     * Ensures the resource path uses the {@code classpath:} prefix, preventing
     * path traversal to arbitrary filesystem locations.
     *
     * @throws KeyLoadException if the path does not start with {@code classpath:}
     */
    private static void validateClasspathLocation(String location) {
        if (location == null || !location.startsWith(CLASSPATH_PREFIX)) {
            throw new KeyLoadException(ErrorCode.INVALID_KEY_FORMAT,
                "Key path must use 'classpath:' prefix: " + location);
        }
    }
}
