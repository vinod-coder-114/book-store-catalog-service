package com.book_store.catalog.service;

public record ImageObject(byte[] content, String contentType, long sizeBytes, String eTag) {
}

