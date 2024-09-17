package com.example.BE_PBL6_FastOrderSystem.security.jwt;

import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import com.example.BE_PBL6_FastOrderSystem.service.Impl.AuthServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.security.jwt.signerKey}")
    private String jwtSecret;

    @NonFinal
    @Value("${spring.security.jwt.valid-duration}")
    private int jwtExpirationMs;

    private final IUserService userService;
    private final AuthServiceImpl authService;

    public JwtUtils(@Lazy AuthServiceImpl authService, IUserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    public String generateJwtTokenForUser(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(Instant.now().plus(jwtExpirationMs, ChronoUnit.SECONDS).toEpochMilli()))
                .setIssuer("FastOrderSystem")
                .setAudience("FastOrderSystem")
                .setNotBefore(new Date(System.currentTimeMillis()))
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setHeaderParam("kid", "fastorder")
                .setId(UUID.randomUUID().toString())
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    private SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateTokenFromRefreshToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(refreshToken).getBody();
        String username = claims.getSubject();
        List<String> roles = (List<String>) claims.get("roles");

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs * 1000L))
                .setIssuer("FastOrderSystem")
                .setAudience("FastOrderSystem")
                .setNotBefore(new Date())
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setHeaderParam("kid", "fastorder")
                .setId(UUID.randomUUID().toString())
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        if (authService.isTokenInvalid(token)) {
            return false;
        }
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid jwt token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Expired token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("This token is not supported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("No claims found: {}", e.getMessage());
        }
        return false;
    }
}