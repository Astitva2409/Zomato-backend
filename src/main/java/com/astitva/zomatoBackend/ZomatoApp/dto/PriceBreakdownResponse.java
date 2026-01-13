package com.astitva.zomatoBackend.ZomatoApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceBreakdownResponse {
    private Double cartValue;           // Sum of item prices
    private List<ChargeBreakdown> charges;  // All applied charges
    private Double finalPrice;          // cartValue + total charges

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargeBreakdown {
        private String chargeName;      // e.g., "Platform Fee", "Surge Fee"
        private Double amount;          // Amount of this charge
    }
}
