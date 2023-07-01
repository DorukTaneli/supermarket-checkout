package com.example.supermarketcheckout.internal;

import lombok.Data;

@Data
public class Scan {
    private char sku;
    private int count;

    public Scan() {
    }

    public Scan(char sku, int count) {
        this.sku = sku;
        this.count = count;
    }
}
