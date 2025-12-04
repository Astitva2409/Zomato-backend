package com.astitva.zomatoBackend.ZomatoApp.strategies;

public interface ChargeStrategy {
    ChargeResult applyCharge(OrderPriceContext context);
}
