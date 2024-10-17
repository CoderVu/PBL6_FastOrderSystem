package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
@NoArgsConstructor
@Data
public class SizeResponse {
    private Long id;
    private String name;
    public SizeResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
 
}
