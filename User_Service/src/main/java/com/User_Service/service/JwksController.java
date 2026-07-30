package com.User_Service.service;


import com.User_Service.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

@RestController
public class JwksController {

    private final JwtService jwtService;

    public JwksController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/oauth2/jwks")
    public ResponseEntity<Map<String, Object>> getJwks() {

        RSAPublicKey publicKey =
                (RSAPublicKey) jwtService.getPublicKey();

        String modulus = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        publicKey.getModulus().toByteArray()
                );

        String exponent = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        publicKey.getPublicExponent().toByteArray()
                );

        Map<String, Object> jwk = Map.of(
                "kty", "RSA",
                "kid", "vbank-jwt",
                "use", "sig",
                "alg", "RS256",
                "n", modulus,
                "e", exponent
        );

        Map<String, Object> response = Map.of(
                "keys", new Object[]{jwk}
        );

        return ResponseEntity.ok(response);
    }
}