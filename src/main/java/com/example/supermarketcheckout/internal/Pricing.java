package com.example.supermarketcheckout.internal;

import lombok.Data;

import java.util.Optional;

@Data
public class Pricing {
    private char sku;
    private int price;
    private SpecialOffer specialOffer;

    public Pricing() {
    }

    public Optional<SpecialOffer> getSpecialOffer() {
        if (specialOffer == null) {
            return Optional.empty();
        } else {
            return Optional.of(specialOffer);
        }
    }

    public Pricing(char sku, int price) {
        this.sku = sku;
        this.price = price;
    }

    public Pricing(char sku, int price, SpecialOffer specialOffer) {
        this.sku = sku;
        this.price = price;
        this.specialOffer = specialOffer;
    }
}
