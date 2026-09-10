package com.book_store.catalog.mapper;

import com.book_store.catalog.domain.Book;
import com.book_store.catalog.domain.BookImageMetadata;
import com.book_store.catalog.domain.BookImageVariant;
import com.book_store.catalog.domain.BookImages;
import com.book_store.catalog.dto.BookDto;
import com.book_store.catalog.dto.BookImageDto;
import com.book_store.catalog.dto.BookUpsertRequest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookMapperTests {

    private final BookMapper mapper = new BookMapper();

    @Test
    void shouldMapRequestToEntityAndImageMetadataToResponse() {
        BookUpsertRequest request = buildRequest();

        Book entity = mapper.toEntity(request);
        entity.setId("bk_10293");

        BookImageMetadata metadata = new BookImageMetadata();
        metadata.setId("img_1");
        metadata.setBookId("bk_10293");
        metadata.setObjectKey("books/bk_10293/0/abc.jpg");
        metadata.setOriginalFilename("cover.jpg");
        metadata.setContentType("image/jpeg");
        metadata.setSizeBytes(1024);
        metadata.setDisplayOrder(0);
        metadata.setPrimaryImage(true);
        metadata.setCreatedAt(Instant.parse("2026-09-09T10:15:30Z"));
        metadata.setUpdatedAt(Instant.parse("2026-09-09T10:20:30Z"));

        BookDto mappedDto = mapper.toDto(entity, List.of(metadata));

        assertNotNull(entity);
        assertEquals("bk_10293", entity.getId());
        assertEquals("Atomic Habits", entity.getTitle());
        assertEquals("James Clear", entity.getAuthor());
        assertEquals("Self Help", entity.getGenre());
        assertEquals("Paperback", entity.getFormat());
        assertEquals("INR", entity.getPricing().getCurrency());
        assertEquals(399, entity.getPricing().getSalePrice());
        assertEquals(499, entity.getPricing().getListPrice());
        assertEquals(4.7, entity.getRating().getAverage());
        assertEquals(12933, entity.getRating().getCount());

        assertNotNull(mappedDto);
        assertEquals("bk_10293", mappedDto.getId());
        assertEquals("Atomic Habits", mappedDto.getTitle());
        assertEquals("James Clear", mappedDto.getAuthor());
        assertEquals("Self Help", mappedDto.getGenre());
        assertEquals("Paperback", mappedDto.getFormat());
        assertEquals("INR", mappedDto.getPricing().getCurrency());
        assertEquals(399, mappedDto.getPricing().getSalePrice());
        assertEquals(499, mappedDto.getPricing().getListPrice());
        assertEquals(4.7, mappedDto.getRating().getAverage());
        assertEquals(12933, mappedDto.getRating().getCount());

        assertNotNull(mappedDto.getImages());
        assertEquals(1, mappedDto.getImages().size());
        BookImageDto image = mappedDto.getImages().getFirst();
        assertEquals("img_1", image.id());
        assertEquals("/catalog/books/bk_10293/images/img_1", image.downloadUrl());
        assertEquals("cover.jpg", image.fileName());
        assertEquals("image/jpeg", image.contentType());
        assertEquals(1024, image.sizeBytes());
        assertEquals(0, image.displayOrder());
        assertTrue(image.primary());
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(mapper.toEntity((BookDto) null));
        assertNull(mapper.toDto(null));
    }

    @Test
    void shouldFallbackToLegacyEmbeddedImagesWhenMetadataIsMissing() {
        Book entity = new Book();
        entity.setId("bk_10293");
        entity.setTitle("Atomic Habits");

        BookImages images = new BookImages();
        BookImageVariant front = new BookImageVariant();
        front.setCard("https://cdn.example.com/books/bk_10293/front-card.avif");
        front.setThumb("https://cdn.example.com/books/bk_10293/front-thumb.avif");
        images.setFront(front);
        entity.setImages(images);

        BookDto dto = mapper.toDto(entity);

        assertNotNull(dto.getImages());
        assertEquals(1, dto.getImages().size());
        assertEquals("https://cdn.example.com/books/bk_10293/front-card.avif", dto.getImages().getFirst().downloadUrl());
    }

    private BookUpsertRequest buildRequest() {
        BookUpsertRequest request = new BookUpsertRequest();
        request.setTitle("Atomic Habits");
        request.setAuthor("James Clear");
        request.setGenre("Self Help");
        request.setFormat("Paperback");

        BookUpsertRequest.Pricing pricing = new BookUpsertRequest.Pricing();
        pricing.setCurrency("INR");
        pricing.setSalePrice(399);
        pricing.setListPrice(499);
        request.setPricing(pricing);

        BookUpsertRequest.Rating rating = new BookUpsertRequest.Rating();
        rating.setAverage(4.7);
        rating.setCount(12933);
        request.setRating(rating);

        return request;
    }
}

