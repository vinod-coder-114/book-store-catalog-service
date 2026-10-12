package com.book_store.catalog.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO class representing a book in the catalog.
 */
@Data
public class BookDto {
    private String id;
    private String title;
    private String author;
    private String genre;
    private String format;
    private Pricing pricing;
    private Rating rating;
    private List<BookImageDto> images;
    private Long stock;

    @Data
    public static class Pricing {
        private String currency;
        private Double salePrice;
        private Double listPrice;
    }

    @Data
    public static class Rating {
        private Double average;
        private Integer count;
    }
}
