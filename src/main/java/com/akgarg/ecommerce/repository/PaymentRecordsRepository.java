package com.akgarg.ecommerce.repository;

import com.akgarg.ecommerce.model.entity.PaymentRecords;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRecordsRepository extends JpaRepository<PaymentRecords, String> {

    PaymentRecords getByOrderId(String orderId);

}