package com.example.BE_PBL6_FastOrderSystem.security.jwt;

import com.example.BE_PBL6_FastOrderSystem.entity.Token;
import com.example.BE_PBL6_FastOrderSystem.repository.TokenRepository;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IAuthService;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final IAuthService authService;
    private final TokenRepository tokenRepository;

    public JwtUtils(@Lazy IUserService userService, @Lazy IAuthService authService, TokenRepository tokenRepository) {
        this.userService = userService;
        this.authService = authService;
        this.tokenRepository = tokenRepository;
    }

    public String generateJwtTokenForUser(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        FoodUserDetails foodUserDetails = (FoodUserDetails) userPrincipal;
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return Jwts.builder()
                .claim("phone", foodUserDetails.getPhoneNumber())
                .claim("roles", roles)
                .claim("sub", foodUserDetails.getSub())
                .claim("facebookId", foodUserDetails.getFacebookId())
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

    public SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateTokenFromRefreshToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(refreshToken).getBody();
        String phone = (String) claims.get("phone");
        List<String> roles = (List<String>) claims.get("roles");

        return Jwts.builder()
                .claim("phone", phone)
                .claim("roles", roles)
                .claim("sub", claims.get("sub"))
                .claim("facebookId", claims.get("facebookId"))
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
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String phone = claims.get("phone", String.class);
        String sub = claims.get("sub", String.class);
        String facebookId = claims.get("facebookId", String.class);

        if (phone != null) {
            return phone;
        } else if (sub != null) {
            return sub;
        } else if (facebookId != null) {
            return facebookId;
        } else {
            return null;
        }
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



    public String generateToken(Authentication authentication) {
        String jwt = generateJwtTokenForUser(authentication);
        Token token = new Token();
        token.setToken(jwt);
        token.setUserId(((FoodUserDetails) authentication.getPrincipal()).getId());
        token.setCreatedAt(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        token.setExpiresAt(LocalDateTime.ofInstant(Instant.now().plus(jwtExpirationMs, ChronoUnit.SECONDS), ZoneId.systemDefault()));
        token.setRevoked(false);
        tokenRepository.save(token);
        return jwt;
    }

}