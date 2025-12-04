package com.astitva.zomatoBackend.ZomatoApp.strategies.Impl;

import com.astitva.zomatoBackend.ZomatoApp.strategies.ChargeResult;
import com.astitva.zomatoBackend.ZomatoApp.strategies.ChargeStrategy;
import com.astitva.zomatoBackend.ZomatoApp.strategies.OrderPriceContext;
import org.springframework.stereotype.Component;

@Component
public class PlatformFeeStrategy implements ChargeStrategy {
    @Override
    public ChargeResult applyCharge(OrderPriceContext context) {
        return new ChargeResult("Platform Fee", 4.0);
    }
}
