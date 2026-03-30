package edu.newnop.infrastructure.adapters.out.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${application.security.jwt.expiration}")
    private int expirationHours;

    private final SecretKey signingKey;
    private final JwtParser jwtParser;

    public JwtService(@Value("${application.security.jwt.secret-key}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);

        this.jwtParser = Jwts.parser()
                .verifyWith(this.signingKey)
                .build();
    }

    private SecretKey getSigningKey() {
        return this.signingKey;
    }

    private Claims extractAllClaims(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String createToken(Map<String, Object> claims, String username, String role) {
        return Jwts.builder()
                .claims(claims) // Updated from setClaims
                .subject(username) // Updated from setSubject
                .claim("role", role) // Fluent claim addition
                .issuedAt(new Date(System.currentTimeMillis()))  // Updated from setIssuedAt
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * expirationHours))
                .signWith(getSigningKey()) // Updated: No algorithm needed if using a SecretKey
                .compact();
    }
}
