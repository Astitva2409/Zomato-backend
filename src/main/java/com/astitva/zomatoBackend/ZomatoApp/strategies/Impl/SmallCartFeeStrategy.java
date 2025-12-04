package com.astitva.zomatoBackend.ZomatoApp.strategies.Impl;

import com.astitva.zomatoBackend.ZomatoApp.strategies.ChargeResult;
import com.astitva.zomatoBackend.ZomatoApp.strategies.ChargeStrategy;
import com.astitva.zomatoBackend.ZomatoApp.strategies.OrderPriceContext;
import org.springframework.stereotype.Component;

@Component
public class SmallCartFeeStrategy implements ChargeStrategy {

    private static final double MIN_CART_VALUE = 150.0;
    private static final double SMALL_CART_FEE = 30.0;

    @Override
    public ChargeResult applyCharge(OrderPriceContext context) {
        double smallCartFee = context.getCartValue() < MIN_CART_VALUE ? SMALL_CART_FEE : 0.0;
        return new ChargeResult("Small Cart Fee", smallCartFee);
    }
}
