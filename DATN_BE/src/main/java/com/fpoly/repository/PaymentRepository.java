package com.fpoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fpoly.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
  
}
