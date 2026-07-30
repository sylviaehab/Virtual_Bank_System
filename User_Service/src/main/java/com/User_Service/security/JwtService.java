package com.User_Service.security;

import com.User_Service.entity.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.keystore.path}")
    private String keystorePath;

    @Value("${jwt.keystore.password}")
    private String keystorePassword;

    @Value("${jwt.key.alias}")
    private String keyAlias;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(User user) {

        PrivateKey privateKey = getPrivateKey();

        return Jwts.builder()
                .header()
                .keyId(keyAlias)
                .and()
                .issuer("user-service")
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis() + expiration
                        )
                )
                .signWith(privateKey)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            try (InputStream inputStream =
                         getClass()
                                 .getClassLoader()
                                 .getResourceAsStream(keystorePath)) {

                if (inputStream == null) {
                    throw new RuntimeException(
                            "Keystore not found: " + keystorePath
                    );
                }

                keyStore.load(
                        inputStream,
                        keystorePassword.toCharArray()
                );
            }

            return (PrivateKey) keyStore.getKey(
                    keyAlias,
                    keystorePassword.toCharArray()
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load private key from keystore",
                    e
            );
        }
    }

    public java.security.PublicKey getPublicKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            try (InputStream inputStream =
                         getClass()
                                 .getClassLoader()
                                 .getResourceAsStream(keystorePath)) {

                if (inputStream == null) {
                    throw new RuntimeException(
                            "Keystore not found: " + keystorePath
                    );
                }

                keyStore.load(
                        inputStream,
                        keystorePassword.toCharArray()
                );
            }

            return keyStore
                    .getCertificate(keyAlias)
                    .getPublicKey();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load public key from keystore",
                    e
            );
        }
    }
}