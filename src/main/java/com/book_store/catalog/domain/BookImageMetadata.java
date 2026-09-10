package com.book_store.catalog.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "book_images")
@CompoundIndex(name = "book_display_order_unique", def = "{'bookId': 1, 'displayOrder': 1}", unique = true)
@Data
public class BookImageMetadata {

    @Id
    private String id;
    private String bookId;
    private String objectKey;
    private String originalFilename;
    private String contentType;
    private long sizeBytes;
    private int displayOrder;
    private boolean primaryImage;
    private Instant createdAt;
    private Instant updatedAt;

}

