package com.astitva.zomatoBackend.ZomatoApp.strategies.Impl;

import com.astitva.zomatoBackend.ZomatoApp.strategies.ChargeResult;
import com.astitva.zomatoBackend.ZomatoApp.strategies.ChargeStrategy;
import com.astitva.zomatoBackend.ZomatoApp.strategies.OrderPriceContext;
import org.springframework.stereotype.Component;

@Component
public class SurgeFeeStrategy implements ChargeStrategy {
    @Override
    public ChargeResult applyCharge(OrderPriceContext context) {
        double surgeFee = context.isPeakHours() ? context.getCartValue() *0.10 : 0.0;
        return new ChargeResult("Surge Fee", surgeFee);
    }
}
