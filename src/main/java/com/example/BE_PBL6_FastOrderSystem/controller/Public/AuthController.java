
package com.example.BE_PBL6_FastOrderSystem.controller.Public;

import com.example.BE_PBL6_FastOrderSystem.repository.RoleRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.*;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.entity.User;
import com.example.BE_PBL6_FastOrderSystem.response.JwtResponse;
import com.example.BE_PBL6_FastOrderSystem.security.jwt.JwtUtils;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IAuthService;
import com.example.BE_PBL6_FastOrderSystem.service.IEmailService;
import com.example.BE_PBL6_FastOrderSystem.service.IFormService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.http.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final IEmailService emailService;
    private final RoleRepository roleRepository;
    private final IFormService formService;


    @PostMapping("/register-user")
    public ResponseEntity<APIRespone> registerUser(@RequestBody User user) {
        return authService.registerUser(user);
    }

    @PostMapping("/register-shipper")
    public ResponseEntity<APIRespone> registerShipper(@RequestBody ShipperRequest shipperRequest) {
        return authService.registerShipper(shipperRequest);

    }


    @PostMapping("/login")
    public ResponseEntity<APIRespone> authenticateUser(@Valid @RequestBody LoginRequest request) {
        return authService.authenticateUser(request.getNumberPhone(), request.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<APIRespone> logoutUser(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            authService.logout(token);
            return new ResponseEntity<>(new APIRespone(true, "Logout success", ""), HttpStatus.OK);
        }
        return new ResponseEntity<>(new APIRespone(true, "Logout success", ""), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<APIRespone> refreshToken(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (jwtUtils.validateToken(refreshToken)) {
            String newAccessToken = jwtUtils.generateTokenFromRefreshToken(refreshToken);
            authService.invalidateToken(refreshToken); // Thu hồi token cũ
            return ResponseEntity.ok(new APIRespone(true, "Token refreshed successfully", newAccessToken));
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Invalid token", null));
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<APIRespone> resetPassword(@RequestParam String email) {
        return authService.SendOTP(email);
    }

    @PostMapping("/confirm-otp")
    public ResponseEntity<APIRespone> verifyOTP(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        return authService.confirmOTP(email, otp, newPassword);
    }

    @GetMapping("/oauth2/callback")
    public void handleCallback(HttpServletResponse response, @AuthenticationPrincipal OAuth2User principal, @RequestParam(value = "client", required = false) String client) throws Exception {
        if (principal == null) {
            response.sendRedirect("https://ambitious-bush-01a78f310.4.azurestaticapps.net/login?error");
            return;
        }

        String email = principal.getAttribute("email");
        String sub = principal.getAttribute("sub");
        String name = principal.getAttribute("name");
        String picture = principal.getAttribute("picture");
        String base64Image = picture != null ? ImageGeneral.urlToBase64(picture) : null;
        User user;
        System.out.println("email: " + email);

        if (sub != null) {
            // Google login
            Optional<User> optionalUser = userRepository.findBySub(sub);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
                user.setAvatar(base64Image);
                user.setSub(sub);
            } else {
                user = new User();
                user.setEmail(email);
                user.setSub(sub);
                user.setFullName(name);
                user.setAvatar(base64Image);
                user.setAccountLocked(false);
                user.setRole(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("ROLE_USER not found")));
            }
        } else {
            // Facebook login
            String facebookId = principal.getAttribute("id");
            System.out.println("Facebook ID: " + facebookId);
            Optional<User> optionalUser = userRepository.findByFacebookId(facebookId);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
                user.setAvatar(base64Image);
                user.setFacebookId(facebookId);
            } else {
                user = new User();
                user.setFullName(name);
                user.setFacebookId(facebookId);
                user.setAvatar(base64Image);
                user.setAccountLocked(false);
                user.setRole(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("ROLE_USER not found")));
            }
        }

        userRepository.save(user);

        // Convert User to FoodUserDetails
        FoodUserDetails userDetails = FoodUserDetails.buildUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String jwt = jwtUtils.generateToken(authentication);

        String redirectUrl;
        if ("flutter".equalsIgnoreCase(client)) {
            redirectUrl = "myapp://oauth2/redirect?token=" + jwt + "&userId=" + user.getId();
        } else {
          redirectUrl = "https://ambitious-bush-01a78f310.4.azurestaticapps.net/oauth2/redirect?token=" + jwt + "&userId=" + user.getId();
          //  redirectUrl = "https://pbl6-fe-user-taupe.vercel.app/oauth2/redirect?token=" + jwt + "&userId=" + user.getId();
        }

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/user-info")
    public ResponseEntity<APIRespone> getUserInfo(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtUtils.key())
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        String sub = claims.get("sub", String.class);
        String facebookId = claims.get("facebookId", String.class);

        Optional<User> optionalUser;
        if (sub != null) {
            System.out.println("Sub: " + sub);
            optionalUser = userRepository.findBySub(sub);
        } else if (facebookId != null) {
            System.out.println("Facebook id: " + facebookId);
            optionalUser = userRepository.findByFacebookId(facebookId);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Invalid token", null));
        }

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return ResponseEntity.ok(new APIRespone(true, "User info retrieved successfully", new JwtResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getPhoneNumber(),
                    user.getAddress(),
                    user.getLongitude(),
                    user.getLatitude(),
                    user.getAvatar(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getAccountLocked(),
                    user.getIsActive(),
                    jwt,
                    List.of("ROLE_USER")
            )));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "User not found", null));
        }
    }


    @PostMapping("/shipper-registration")
    public ResponseEntity<APIRespone> addForm(
            @RequestParam("name") String name,
            @RequestParam("citizenID") String citizenID,
            @RequestParam("imageCitizenFront") MultipartFile imageCitizenFront,
            @RequestParam("imageCitizenBack") MultipartFile imageCitizenBack,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("age") Integer age,
            @RequestParam("vehicle") String vehicle,
            @RequestParam("licensePlate") String licensePlate,
            @RequestParam("driverLicense") String driverLicense) {
        FormRequest formRequest = new FormRequest(name, citizenID, imageCitizenFront, imageCitizenBack, email, phone, address, age, vehicle, licensePlate, driverLicense);
        ResponseEntity<APIRespone> response = formService.addForm(formRequest);
        if (response.getBody().getMessage().equals("Success")) {
            String adminEmail = "vunguyen.170803@gmail.com";
            String subject = "New shipper registration";
            String message = "<html><head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; }" +
                    "h1 { color: #333; }" +
                    "p { font-size: 14px; }" +
                    "ul { list-style-type: none; padding: 0; }" +
                    "li { margin-bottom: 10px; }" +
                    "strong { color: #555; }" +
                    "</style>" +
                    "</head><body>" +
                    "<h1>New Shipper Registration</h1>" +
                    "<p>A new shipper has registered with the following details:</p>" +
                    "<ul>" +
                    "<li><strong>Name:</strong> " + name + "</li>" +
                    "<li><strong>Email:</strong> " + email + "</li>" +
                    "<li><strong>Phone:</strong> " + phone + "</li>" +
                    "<li><strong>Address:</strong> " + address + "</li>" +
                    "</ul>" +
                    "<p>Please review and approve the registration.</p>" +
                    "</body></html>";
            emailService.sendEmail(adminEmail, subject, message);
        }
        return response;
    }
    @PostMapping("/shipper-registration2")
    public ResponseEntity<APIRespone> addFormV2(
            @RequestBody FormRequestV2 request) {

        return formService.addFormV2(request);
    }
}
