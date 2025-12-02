package com.astitva.zomatoBackend.ZomatoApp.repository;

import com.astitva.zomatoBackend.ZomatoApp.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
