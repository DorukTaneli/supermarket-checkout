package com.example.supermarketcheckout.internal;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Document(collection = "checkouts")
public class Checkout {
    @Id
    private String checkoutID;
    private List<Pricing> pricingList;
    private List<Scan> scanList;

    public Checkout() {
        this.pricingList = new ArrayList<>();
        this.scanList = new ArrayList<>();
    }

    public Optional<Pricing> findPricingBySku(char sku){
        for (Pricing pricing : pricingList) {
            if (pricing.getSku() == sku) {
                return Optional.of(pricing);
            }
        }
        return Optional.empty();
    }

    public Optional<Scan> findScanBySku(char sku){
        for (Scan scan : scanList) {
            if (scan.getSku() == sku) {
                return Optional.of(scan);
            }
        }
        return Optional.empty();
    }
}
