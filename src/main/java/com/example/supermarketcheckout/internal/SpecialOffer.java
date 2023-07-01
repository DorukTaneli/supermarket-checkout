package com.example.supermarketcheckout.internal;

import lombok.Data;

@Data
public class SpecialOffer {
    private int count;
    private int price;

    public SpecialOffer(int count, int price) {
        this.count = count;
        this.price = price;
    }
}
