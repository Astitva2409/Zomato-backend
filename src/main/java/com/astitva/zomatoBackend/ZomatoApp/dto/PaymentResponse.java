package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.PaymentMethod;
import com.astitva.zomatoBackend.ZomatoApp.entities.enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionId;
}
