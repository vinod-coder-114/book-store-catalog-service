package com.book_store.catalog.dto;

import java.time.Instant;

public record BookImageDto(
		String id,
		String downloadUrl,
		String fileName,
		String contentType,
		long sizeBytes,
		int displayOrder,
		boolean primary,
		Instant createdAt,
		Instant updatedAt
) {
}

