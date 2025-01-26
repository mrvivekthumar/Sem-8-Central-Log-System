package com.example.authenticationservice.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtService {

    private static final long TOKEN_VALIDITY = 1000 * 60 * 30;
    public static final String SECRET = "42fd281ad04c98138f1556dc95b9a535da7036974cdcca8d1c57f508689bb98e9f31fdcace377d432dfcfd90195a566e69d15a9bbd9de6d50262502eea823001ab7aa1209903ffa4b5767b169cd99f485efcf6dad4152eb80f4b9198c0707838251abaf4e278652039a0277715674b39e26f7121cd6777c57a349223f37a120fbed63e87154bbe72000906cdbc7edbaaffd8455eb8e40f3d56c00d33c6ea3bc6fceb45223df570a3404a159a023253e38c91bb2fc22fdfcb6386c90b82e745ba7239c5bbd437d6af4bca600bf8dee44ee6c5f7bcb75984f3dbb499707415f96d0dd09249d8ab235b8a9df3faed75c02b22946feb714127d5bedf3d3c4a369d80";

    public boolean validateToken(String token, String userRole) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);
            return userRole.equalsIgnoreCase(role);
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Token validation failed: " + e.getMessage());
        }
    }



    public String generateToken(String username, String userRole) {
        Map<String, Object> claims = Map.of("role", userRole);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}