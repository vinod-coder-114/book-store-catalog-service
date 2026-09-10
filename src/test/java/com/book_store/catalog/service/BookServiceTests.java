package com.book_store.catalog.service;

import com.book_store.catalog.domain.Book;
import com.book_store.catalog.domain.BookImageMetadata;
import com.book_store.catalog.domain.BookPricing;
import com.book_store.catalog.domain.BookRating;
import com.book_store.catalog.dto.BookDto;
import com.book_store.catalog.dto.BookUpsertRequest;
import com.book_store.catalog.mapper.BookMapper;
import com.book_store.catalog.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookServiceTests {

    private final BookRepository bookRepository = mock(BookRepository.class);
    private final BookImageService bookImageService = mock(BookImageService.class);
    private final BookService bookService = new BookService(bookRepository, new BookMapper(), bookImageService);

    @Test
    void createBookStoresImagesAndReturnsUiDto() {
        BookUpsertRequest request = buildRequest();
        MockMultipartFile image = new MockMultipartFile("images", "cover.jpg", "image/jpeg", "hello".getBytes());

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            book.setId("bk_1");
            return book;
        });

        BookImageMetadata metadata = new BookImageMetadata();
        metadata.setId("img_1");
        metadata.setBookId("bk_1");
        metadata.setObjectKey("books/bk_1/0/abc.jpg");
        metadata.setOriginalFilename("cover.jpg");
        metadata.setContentType("image/jpeg");
        metadata.setSizeBytes(5);
        metadata.setDisplayOrder(0);
        metadata.setPrimaryImage(true);
        metadata.setCreatedAt(Instant.parse("2026-09-09T10:15:30Z"));
        metadata.setUpdatedAt(Instant.parse("2026-09-09T10:15:30Z"));
        when(bookImageService.uploadImages("bk_1", List.of(image))).thenReturn(List.of(metadata));

        BookDto response = bookService.createBook(request, List.of(image));

        assertThat(response.getId()).isEqualTo("bk_1");
        assertThat(response.getTitle()).isEqualTo("Atomic Habits");
        assertThat(response.getImages()).hasSize(1);
        assertThat(response.getImages().getFirst().downloadUrl()).isEqualTo("/catalog/books/bk_1/images/img_1");
    }

    @Test
    void getAllBooksHydratesImageMetadataInBulk() {
        Book book = new Book();
        book.setId("bk_1");
        book.setTitle("Atomic Habits");
        BookPricing pricing = new BookPricing();
        pricing.setCurrency("INR");
        pricing.setSalePrice(100);
        pricing.setListPrice(120);
        book.setPricing(pricing);
        BookRating rating = new BookRating();
        rating.setAverage(4.8);
        rating.setCount(10);
        book.setRating(rating);

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookImageService.findImagesByBookIds(List.of("bk_1"))).thenReturn(Map.of());

        List<BookDto> response = bookService.getAllBooks();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getTitle()).isEqualTo("Atomic Habits");
        assertThat(response.getFirst().getImages()).isEmpty();
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
