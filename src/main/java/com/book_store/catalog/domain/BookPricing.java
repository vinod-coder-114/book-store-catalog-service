package com.book_store.catalog.domain;

import lombok.Data;

@Data
public class BookPricing {
    private String currency = "INR";
    private Integer salePrice;
    private Integer listPrice;
}

