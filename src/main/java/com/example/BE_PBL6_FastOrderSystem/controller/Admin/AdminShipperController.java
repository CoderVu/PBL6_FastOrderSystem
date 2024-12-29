package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.repository.CodeShipperRepository;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IAuthService;
import com.example.BE_PBL6_FastOrderSystem.service.IEmailService;
import com.example.BE_PBL6_FastOrderSystem.service.IFormService;
import com.example.BE_PBL6_FastOrderSystem.service.IOTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/shippers")
@RequiredArgsConstructor
public class AdminShipperController {
    private final IFormService formService;
    private final IAuthService authService;
    private final IEmailService emailService;
    private final IOTPService otpService;

    @GetMapping("/all")
    public ResponseEntity<APIRespone> getAllShippers() {
        return formService.getAllForms();
    }
    @PostMapping("/send-email-to-shipper")
    public ResponseEntity<APIRespone> sendEmailToShipper(
            @RequestParam("email") String email,
            @RequestParam("name") String name) {
        String subject = "Xac nhan duoc duyet dang ky lam shipper";
        String code = otpService.generateCodeShipper();

        String message = "<html><body>" +
                "<h1>Thong tin da duoc duyet</h1>" +
                "<p>Xin chao " + name + "</p>" +
                "<p>Chuc mung ban da duoc duyet lam shipper cua chung toi</p>" +
                "<p>Ma xac nhan cua ban la: " + code + "</p>" +
                "<p>Vui long nhap ma xac nhan tren ung dung de hoan tat qua trinh dang ky</p>" +
                "</body></html>";
        emailService.sendEmail(email, subject, message);

        return ResponseEntity.ok(new APIRespone(true, "Email sent successfully", ""));
    }

    @PostMapping("/approve-shippers/{userId}")
    public ResponseEntity<APIRespone> approveShipper(@PathVariable Long userId) {
        return authService.approveShipper(userId);
    }
}
