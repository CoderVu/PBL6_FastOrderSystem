package com.example.BE_PBL6_FastOrderSystem.security.jwt;
import com.example.BE_PBL6_FastOrderSystem.model.RefreshToken;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.request.RefeshRequest;
import com.example.BE_PBL6_FastOrderSystem.response.JwtResponse;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.ILogoutService;
import com.example.BE_PBL6_FastOrderSystem.service.IRefreshTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("4+jwHqc1H0a4UkXCmiV81DSWMMdrqZJFC3IhFkby04I=")
    private String jwtSecret;

    @Value("10") //
    private int jwtExpirationMs;
    @Autowired // để sử dụng logoutService
    private ILogoutService logoutService;
    @Autowired
    private IRefreshTokenService refreshTokenService;
    public String generateJwtTokenForUser(Authentication authentication){
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", roles) // Add roles to the token
                .setIssuedAt(new Date()) // Set the time when the token was generated
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Set the expiration time of the token
                .signWith(key(), SignatureAlgorithm.HS256) // Sign the token with the secret key
                .setId(UUID.randomUUID().toString()) // Add random ID to the token để dễ dàng quản lý token trong DB nếu cần thiết (ví dụ: logout)
                .compact();
    }

    private SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public JwtResponse  refreshToken(RefeshRequest refeshRequest){
        String refreshToken = refeshRequest.getRefreshToken();
        if(logoutService.isTokenInvalid(refreshToken)){
            throw new JwtException("Invalid refresh token");
        }
        try{
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(refreshToken);
            String username = claims.getBody().getSubject();
            User user = new User();
            user.setPhoneNumber(username);
            RefreshToken token = refreshTokenService.findByUser(user);
            if(token == null){
                throw new JwtException("Invalid refresh token");
            }
            if(!token.getToken().equals(refreshToken)){
                throw new JwtException("Invalid refresh token");
            }
            FoodUserDetails userDetails = new FoodUserDetails(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = generateJwtTokenForUser(authentication);
            return new JwtResponse(jwtToken, refreshToken);
        } catch (JwtException e){
            throw new JwtException("Invalid refresh token");
        }
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
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
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