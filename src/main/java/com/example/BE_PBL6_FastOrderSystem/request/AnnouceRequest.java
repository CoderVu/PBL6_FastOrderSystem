package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

@Data
public class AnnouceRequest {
    Long userid;
    String title;
    String content;

    public AnnouceRequest(Long userid, String title, String content) {
        this.userid = userid;
        this.title = title;
        this.content = content;
    }
}