package com.book_store.catalog.domain;

import lombok.Data;

@Data
public class BookImages {
    private BookImageVariant front;
    private BookImageVariant back;
    private Double aspectRatio;
    private String blurHash;
}

