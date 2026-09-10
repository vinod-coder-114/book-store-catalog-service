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

    @Data
    public static class Pricing {
        private String currency;
        private Integer salePrice;
        private Integer listPrice;
    }

    @Data
    public static class Rating {
        private Double average;
        private Integer count;
    }
}
