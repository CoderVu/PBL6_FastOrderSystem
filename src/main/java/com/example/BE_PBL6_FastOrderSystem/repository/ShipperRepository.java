package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShipperRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT USER.*, \n" +
            "ROUND(( 6371 * acos( cos( radians(:latitude) ) * cos( radians( latitude ) ) * cos( radians( longitude ) - radians(:longitude) ) + sin( radians(:latitude) ) * sin( radians( latitude ) ) )), 4) AS distance \n" +
            "FROM USER \n" +
            "WHERE is_active = true \n" +
            "AND role_id = (SELECT id FROM role WHERE name = 'ROLE_SHIPPER') \n" +
            "ORDER BY distance \n" +
            "LIMIT :limit", nativeQuery = true)
    List<User> findNearestShippers(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("limit") int limit);

}