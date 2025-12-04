package com.astitva.zomatoBackend.ZomatoApp.strategies;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChargeResult {
    private String chargeName;
    private double amount;
}
