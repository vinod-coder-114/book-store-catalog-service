package com.book_store.catalog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookUpsertRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @NotBlank
    private String genre;
    @NotBlank
    private String format;
    @NotNull
    @Valid
    private Pricing pricing;
    @NotNull
    @Valid
    private Rating rating;

    @Data
    public static class Pricing {
        @NotBlank
        private String currency;
        @NotNull
        private Integer salePrice;
        @NotNull
        private Integer listPrice;
    }

    @Data
    public static class Rating {
        @NotNull
        private Double average;
        @NotNull
        private Integer count;
    }
}
