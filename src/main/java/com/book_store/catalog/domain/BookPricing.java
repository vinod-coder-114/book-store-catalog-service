package com.book_store.catalog.domain;

import lombok.Data;

@Data
public class BookPricing {
    private String currency = "INR";
    private Double salePrice;
    private Double listPrice;
}

