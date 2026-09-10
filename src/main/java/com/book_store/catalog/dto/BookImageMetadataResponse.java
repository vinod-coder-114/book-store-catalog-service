package com.book_store.catalog.dto;

import com.book_store.catalog.domain.BookImageMetadata;

import java.time.Instant;

public record BookImageMetadataResponse(
        String id,
        String bookId,
        String downloadUrl,
        String fileName,
        String contentType,
        long sizeBytes,
        int displayOrder,
        boolean primary,
        Instant createdAt,
        Instant updatedAt
) {

    public static BookImageMetadataResponse from(BookImageMetadata metadata) {
        return new BookImageMetadataResponse(
                metadata.getId(),
                metadata.getBookId(),
                "/catalog/books/%s/images/%s".formatted(metadata.getBookId(), metadata.getId()),
                metadata.getOriginalFilename(),
                metadata.getContentType(),
                metadata.getSizeBytes(),
                metadata.getDisplayOrder(),
                metadata.isPrimaryImage(),
                metadata.getCreatedAt(),
                metadata.getUpdatedAt()
        );
    }
}

