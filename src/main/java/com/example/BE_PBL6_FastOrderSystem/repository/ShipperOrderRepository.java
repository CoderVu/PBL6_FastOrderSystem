package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.ShipperOrder;
import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ShipperOrderRepository extends JpaRepository<ShipperOrder, Long> {

    List<ShipperOrder> findByShipperId(Long shipperId);

    List<ShipperOrder> findAllByShipperId(Long shipperId);
}