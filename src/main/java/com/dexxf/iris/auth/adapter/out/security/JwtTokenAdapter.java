package com.dexxf.iris.auth.adapter.out.security;

import com.dexxf.iris.auth.application.port.out.JwtTokenGeneratorPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenAdapter implements JwtTokenGeneratorPort {

    private final SecretKey secretKey;
    private final long jwtExpiration;

    public JwtTokenAdapter(@Value("${security.jwt.secret-key}") String secretKey, @Value("${security.jwt.expiration-ms}") long jwtExpiration) {
        this.secretKey = getKey(secretKey);
        this.jwtExpiration = jwtExpiration;
    }

    @Override
    public String generateToken(String authId) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder().claims()
                .add(claims)
                .subject(authId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .and()
                .signWith(secretKey,Jwts.SIG.HS256).compact();
    }


    private SecretKey getKey(String raw) {
        byte[] keyByte = Decoders.BASE64.decode(raw);
        return Keys.hmacShaKeyFor(keyByte);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> function) {
        Claims claims = extractAllClaims(token);
        return function.apply(claims);
    }

    public String extractAuthId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String authId = extractAuthId(token);

        return userDetails.getUsername()
                .equals(authId) &&
                !isTokenExpired(token);
    }
}
