package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.PaymentMethod;
import lombok.Data;

@Data
public class PaymentRequest {
    private Long orderId;
    private PaymentMethod paymentMethod;
}
