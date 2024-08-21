package com.example.BE_PBL6_FastOrderSystem.security.jwt;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Set content type for the response
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Check if the error is 404 (Not Found)
        if (response.getStatus() == HttpServletResponse.SC_NOT_FOUND) {
            handleNotFound(request, response);
        } else {
            // Handle 401 Unauthorized error
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            ResponseEntity<APIRespone> responseEntity = buildResponseEntity(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized",
                    authException.getMessage(),
                    request.getServletPath()
            );

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), responseEntity.getBody());
        }
    }

    private void handleNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        ResponseEntity<APIRespone> responseEntity = buildResponseEntity(
                HttpServletResponse.SC_NOT_FOUND,
                "Not Found",
                "The requested URL was not found on this server.",
                request.getServletPath()
        );

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), responseEntity.getBody());
    }
    private ResponseEntity<APIRespone> buildResponseEntity(int status, String error, String message, String path) {
        APIRespone apiResponse = APIRespone.builder()
                .status(false)
                .message(message)
                .data(Map.of(
                        "status", status,
                        "error", error,
                        "path", path

                ))
                .build();
        return ResponseEntity.status(status).body(apiResponse);
    }
}