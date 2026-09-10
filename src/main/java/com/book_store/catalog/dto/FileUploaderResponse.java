package com.book_store.catalog.dto;

import java.util.List;

public record FileUploaderResponse(String id, List<String> fileNames,
                                   List<String> fileUrls) {
}
