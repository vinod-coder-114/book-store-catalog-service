package com.book_store.catalog.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "books")
public class Book {

    @Id
    private String id;

    @TextIndexed(weight = 5)
    private String title;

    @TextIndexed(weight = 3)
    private String author;

    private String genre;

    private String format;

    private BookPricing pricing;

    private BookRating rating;

    private BookImages images;
}


