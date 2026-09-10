package com.book_store.catalog.service;

import com.book_store.catalog.config.CatalogImageProperties;
import com.book_store.catalog.domain.BookImageMetadata;
import com.book_store.catalog.exception.BadRequestException;
import com.book_store.catalog.repository.BookImageMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookImageServiceTests {

    private BookImageMetadataRepository repository;
    private ObjectStorageService objectStorageService;
    private BookImageService service;

    @BeforeEach
    void setUp() {
        repository = mock(BookImageMetadataRepository.class);
        objectStorageService = mock(ObjectStorageService.class);
        CatalogImageProperties properties = new CatalogImageProperties(1024 * 1024, List.of("image/jpeg", "image/png"));
        service = new BookImageService(repository, objectStorageService, properties);
    }

    @Test
    void uploadStoresObjectAndMetadata() {
        MockMultipartFile file = new MockMultipartFile("file", "cover.jpg", "image/jpeg", "hello".getBytes());
        when(repository.findTopByBookIdOrderByDisplayOrderDesc("book-1")).thenReturn(Optional.empty());
        when(repository.save(org.mockito.ArgumentMatchers.any(BookImageMetadata.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<BookImageMetadata> metadata = service.uploadImages("book-1", List.of(file));

        verify(objectStorageService).putObject(org.mockito.ArgumentMatchers.contains("books/book-1/0/"),
                org.mockito.ArgumentMatchers.argThat(bytes -> java.util.Arrays.equals(bytes, "hello".getBytes())),
                org.mockito.ArgumentMatchers.eq("image/jpeg"));

        ArgumentCaptor<BookImageMetadata> captor = ArgumentCaptor.forClass(BookImageMetadata.class);
        verify(repository).save(captor.capture());

        assertThat(metadata).hasSize(1);
        assertThat(metadata.getFirst().getBookId()).isEqualTo("book-1");
        assertThat(metadata.getFirst().getDisplayOrder()).isEqualTo(0);
        assertThat(metadata.getFirst().isPrimaryImage()).isTrue();
        assertThat(captor.getValue().getObjectKey()).contains("books/book-1/0/");
    }

    @Test
    void uploadRejectsUnsupportedType() {
        MockMultipartFile file = new MockMultipartFile("file", "cover.gif", "image/gif", "hello".getBytes());

        assertThatThrownBy(() -> service.uploadImages("book-1", List.of(file)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Unsupported content type");
    }
}

