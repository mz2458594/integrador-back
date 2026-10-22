package com.integrador.rocket.roadmap.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.integrador.rocket.roadmap.models.users.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.time.Duration;



@Service
public class TokenService {


    @Value("${api.security.token.secret}")
    private String secret;


    public String generarToken(User user) {
        return generarToken(user, Duration.ofHours(2));
    }

    public String generarToken(User user, Duration lifetime) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("MZP")
                    .withSubject(user.getEmail())
                    .withExpiresAt(Instant.now().plus(lifetime))
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
            throw new RuntimeException("Error al generar el token " + ex);
        }
    }

    public String getSubject(String tokenJwt) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("MZP")
                    .build()
                    .verify(tokenJwt)
                    .getSubject();
        } catch (JWTVerificationException ex) {
            throw new RuntimeException("Token inválido o expirado");
        }
    }


}
