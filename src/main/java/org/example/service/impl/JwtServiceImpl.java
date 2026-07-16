package org.example.service.impl;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.config.JwtProperties;
import org.example.service.JwtService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    private SecretKey accessKey;
    private SecretKey refreshKey;

    private SecretKey getAccessKey() {
        if (accessKey == null) {
            accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getAccessSecret()));
        }

        return accessKey;
    }

    private SecretKey getRefreshKey() {
        if (refreshKey == null) {
            refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getRefreshSecret()));
        }

        return refreshKey;
    }

    @Override
    public String generateAccessToken(String username, List<String> roles) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtProperties.getAccessTokenExpiration())))
                .id(UUID.randomUUID().toString())
                .signWith(getAccessKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(String username) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtProperties.getRefreshTokenExpiration())))
                .id(UUID.randomUUID().toString())
                .signWith(getRefreshKey())
                .compact();
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().verifyWith(getAccessKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().verifyWith(getRefreshKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getUsernameFromAccessToken(String token) {
        return Jwts.parser().verifyWith(getAccessKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    @Override
    public String getUsernameFromRefreshToken(String token) {
        return Jwts.parser().verifyWith(getRefreshKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getRolesFromAccessToken(String token) {
        return Jwts.parser().verifyWith(getAccessKey()).build().parseSignedClaims(token).getPayload().get("roles", List.class);
    }

}
