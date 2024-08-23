package com.example.BE_PBL6_FastOrderSystem.security.jwt;

import com.example.BE_PBL6_FastOrderSystem.service.ILogoutService;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Value("${jwt.signerKey}")
    private String jwtSecret;

    @NonFinal
    @Value("${jwt.valid-duration}")
    private int jwtExpirationMs;
    @Autowired // để sử dụng logoutService
    private IUserService userService;
    @Autowired
    private ILogoutService logoutService;
    public String generateJwtTokenForUser(Authentication authentication){
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", roles) // Add roles to the token
                .setIssuedAt(new Date(System.currentTimeMillis())) // là thời gian bắt đầu của token
                .setExpiration(new Date(Instant.now().plus(jwtExpirationMs, ChronoUnit.SECONDS).toEpochMilli())) // là thời gian kết thúc của token
                .setIssuer("FastOrderSystem") // Set issuer
                .setAudience("FastOrderSystem") // Set audience
                .setNotBefore(new Date(System.currentTimeMillis())) // Set not before
                .setHeaderParam("typ", "JWT") // Set header
                .setHeaderParam("alg", "HS256") // Set header
                .setHeaderParam("kid", "fastorder") // Set header
                .setId(UUID.randomUUID().toString()) // Add random ID to the token để dễ dàng quản lý token trong DB nếu cần thiết (ví dụ: logout)

                .signWith(key(), SignatureAlgorithm.HS256) // Sign the token with the secret key
                .setId(UUID.randomUUID().toString()) // Add random ID to the token để dễ dàng quản lý token trong DB nếu cần thiết (ví dụ: logout)
                .compact();
    }

    private SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // JwtUtils.java

    public String generateTokenFromRefreshToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(refreshToken).getBody();
        return Jwts.builder()
                .setSubject(claims.getSubject())
                .claim("roles", claims.get("roles"))
                .setSubject(claims.getSubject())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs * 1000L)) // là thời gian kết thúc của token là thời gian hiện tại cộng thêm thời gian hết hạn của token
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }
    public String getUserNameFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        if(logoutService.isTokenInvalid(token)){
            return false;
        }
        try{
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch(MalformedJwtException e){
            logger.error("Invalid jwt token: {}", e.getMessage());
        } catch (ExpiredJwtException e){
            logger.error("Expired token: {}", e.getMessage());
        } catch (UnsupportedJwtException e){
            logger.error("This token is not supported: {}", e.getMessage());
        } catch (IllegalArgumentException e){
            logger.error("No claims found: {}", e.getMessage());
        }
        return false;
    }
}