package com.book_store.catalog.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "catalog.image")
public record CatalogImageProperties(
        @Positive long maxSizeBytes,
        @NotEmpty List<String> allowedContentTypes
) {
}

