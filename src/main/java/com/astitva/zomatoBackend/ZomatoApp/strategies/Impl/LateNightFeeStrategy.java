package com.astitva.zomatoBackend.ZomatoApp.strategies.Impl;

import com.astitva.zomatoBackend.ZomatoApp.strategies.ChargeResult;
import com.astitva.zomatoBackend.ZomatoApp.strategies.ChargeStrategy;
import com.astitva.zomatoBackend.ZomatoApp.strategies.OrderPriceContext;
import org.springframework.stereotype.Component;

@Component
public class LateNightFeeStrategy implements ChargeStrategy {
    @Override
    public ChargeResult applyCharge(OrderPriceContext context) {
        int hour = context.getOrderTime().getHour();
        double fee = (hour >= 23 || hour <= 5) ? 20.0 : 0.0;

        return new ChargeResult("Late Night fee", fee);
    }
}
