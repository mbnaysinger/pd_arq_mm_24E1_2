package com.store.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>{

}
