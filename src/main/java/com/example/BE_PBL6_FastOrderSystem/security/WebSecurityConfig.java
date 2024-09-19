package com.example.BE_PBL6_FastOrderSystem.security;

import com.example.BE_PBL6_FastOrderSystem.security.jwt.AuthTokenFilter;
import com.example.BE_PBL6_FastOrderSystem.security.jwt.JwtAuthEntryPoint;
import com.example.BE_PBL6_FastOrderSystem.security.jwt.JwtUtils;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetailsService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebSecurityConfig {
    private final FoodUserDetailsService userDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtUtils jwtUtils;
    private static final String[] AUTH = {
            "/api/v1/auth/**"
    };
    private static final String[] ADMIN = {
            "/api/v1/admin/**"
    };
    private static final String[] OWNER = {
            "/api/v1/owner/**"
    };
    private static final String[] USER = {
            "/api/v1/user/**"
    };
    private static final String[] PUBLIC = {
            "/api/v1/public/**"
    };
    private static final String[] MOMO = {
             "/api/v1/momo/**"
    };
    private static final String[] ZALO = {
            "/api/v1/zalopay/**"
    };
    @Autowired
    public WebSecurityConfig(@Lazy JwtUtils jwtUtils, JwtAuthEntryPoint jwtAuthEntryPoint, FoodUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable)
//                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v1/auth/**").permitAll()
//                        .requestMatchers("/api/v1/public/**").permitAll()
//                        .requestMatchers("/api/v1/momo/**").permitAll()
//                        .requestMatchers("/api/v1/zalopay/**").permitAll()
//                        .requestMatchers("/api/v1/user/**").hasAnyRole("ADMIN", "USER", "OWNER")
//                        .requestMatchers("/api/v1/owner/**").hasAnyRole("OWNER", "ADMIN")
//                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")
//                        .requestMatchers("/api/v1/auth/login-google").authenticated()
//                        .anyRequest().authenticated()
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/api/v1/auth/login-google")
//                        .defaultSuccessUrl("/api/v1/auth/login-google-success", true)
//                        .failureUrl("/api/v1/auth/login-google-failure")
//                );
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH).permitAll()
                        .requestMatchers(PUBLIC).permitAll()
                        .requestMatchers(MOMO).permitAll()
                        .requestMatchers(ZALO).permitAll()
                        .requestMatchers(USER).hasAnyRole("ADMIN", "USER", "OWNER")
                        .requestMatchers(OWNER).hasAnyRole("OWNER", "ADMIN")
                        .requestMatchers(ADMIN).hasAnyRole("ADMIN")
//                        .requestMatchers("/api/v1/auth/").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/api/v1/auth/login-google")
                        .defaultSuccessUrl("/api/v1/auth/login-google-success", true)
                        .failureUrl("/api/v1/auth/login-google-failure")
                );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
    
