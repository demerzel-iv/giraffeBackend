package com.giraffe.restservice.service;

import java.util.Date;
import java.util.HashMap;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "jwt")
public class JwtService {
    private String secretKey;

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String idToJwtToken(int id) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        HashMap<String, Object> header = new HashMap<>(2);
        header.put("Type", "Jwt");
        header.put("alg", "HS256");

        return JWT.create()
                .withHeader(header)
                .withClaim("id", id)
                .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 86400 * 1000))
                .sign(algorithm);
    }

    public int jwtTokenToId(String jwtToken) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(jwtToken);
        return jwt.getClaim("id").asInt();
    }
    
}
