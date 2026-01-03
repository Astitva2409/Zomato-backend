package com.astitva.zomatoBackend.ZomatoApp.strategies;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PricingEngine {

    private final List<ChargeStrategy> strategies;

    public PriceBreakdown calculateDetailedPrice(OrderPriceContext context) {
        double cartValue = context.getCartValue();
        double totalCharge = 0.0;

        List<ChargeResult> chargeResults = new ArrayList<>();
        for(ChargeStrategy strategy : strategies) {
            ChargeResult result = strategy.applyCharge(context);
            chargeResults.add(result);
            totalCharge += result.getAmount();
        }

        double finalPrice = cartValue + totalCharge;
        return PriceBreakdown.builder()
                .cartValue(cartValue)
                .charges(chargeResults)
                .finalPrice(finalPrice)
                .build();
    }
}
