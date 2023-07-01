package com.example.supermarketcheckout.requestbody;

import lombok.Data;

@Data
public class ScanRequestBody {
    private char sku;

    public ScanRequestBody() {
    }

    public ScanRequestBody(char sku) {
        this.sku = sku;
    }
}
