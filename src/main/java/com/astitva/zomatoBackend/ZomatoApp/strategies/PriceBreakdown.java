package com.astitva.zomatoBackend.ZomatoApp.strategies;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PriceBreakdown {
    private double cartValue;
    private List<ChargeResult> charges;
    private double finalPrice;
}
