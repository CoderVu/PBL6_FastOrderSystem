package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Long> {
    @Query("SELECT pd FROM PaymentDetail pd WHERE pd.payment.paymentId = ?1")
    List<PaymentDetail> findByPaymentId(Long paymentId);
}
