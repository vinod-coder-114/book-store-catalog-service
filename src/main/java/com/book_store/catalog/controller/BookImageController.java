package com.book_store.catalog.controller;

import com.book_store.catalog.dto.BookImageDto;
import com.book_store.catalog.domain.BookImageMetadata;
import com.book_store.catalog.service.BookImageService;
import com.book_store.catalog.service.ImageObject;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/catalog/books/{bookId}/images")
public class BookImageController {

    private final BookImageService bookImageService;

    public BookImageController(BookImageService bookImageService) {
        this.bookImageService = bookImageService;
    }

    @GetMapping
    public ResponseEntity<List<BookImageDto>> listImages(@PathVariable String bookId) {
        return ResponseEntity.ok(bookImageService.listImages(bookId).stream().map(this::toDto).toList());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<BookImageDto>> upload(
            @PathVariable String bookId,
            @RequestPart("images") List<MultipartFile> images
    ) {
        return ResponseEntity.ok(bookImageService.uploadImages(bookId, images).stream().map(this::toDto).toList());
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String bookId, @PathVariable String imageId) {
        ImageObject image = bookImageService.getImage(bookId, imageId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
                .header(HttpHeaders.ETAG, image.eTag())
                .contentType(MediaType.parseMediaType(image.contentType()))
                .contentLength(image.sizeBytes())
                .body(image.content());
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable String bookId, @PathVariable String imageId) {
        bookImageService.deleteImage(bookId, imageId);
        return ResponseEntity.noContent().build();
    }

    private BookImageDto toDto(BookImageMetadata metadata) {
        return new BookImageDto(
                metadata.getId(),
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

