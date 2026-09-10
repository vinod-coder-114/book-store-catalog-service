package com.book_store.catalog.service;

public interface ObjectStorageService {

    void putObject(String objectKey, byte[] content, String contentType);

    ImageObject getObject(String objectKey);

    void deleteObject(String objectKey);
}

