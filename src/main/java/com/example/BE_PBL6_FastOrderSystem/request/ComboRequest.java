package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComboRequest {
    private String comboName;
    private Double price;
    private MultipartFile image;
}
