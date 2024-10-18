package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.entity.AnnounceUser;
import com.example.BE_PBL6_FastOrderSystem.repository.AnnounceRepository;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IAnnouceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnounceServiceImpl implements IAnnouceService {
    private final AnnounceRepository announceRepository;

    @Override
    public ResponseEntity<APIRespone> getbyuserId(Long userId) {
        List<AnnounceUser> announceUsers = announceRepository.findByUserid(userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new APIRespone(true,"Danh sách thông báo",announceUsers)
        );
    }

    @Override
    public ResponseEntity<APIRespone> addnewannounce(Long userId, String title, String content) {
        AnnounceUser announceUser = new AnnounceUser();
        announceUser.setUserid(userId);
        announceUser.setTitle(title);
        announceUser.setContent(content);
        announceRepository.save(announceUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new APIRespone(true,"Thêm thông báo thành công","")
        );
    }

}