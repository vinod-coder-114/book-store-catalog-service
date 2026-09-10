package com.book_store.catalog.repository;

import com.book_store.catalog.domain.BookImageMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookImageMetadataRepository extends MongoRepository<BookImageMetadata, String> {

    List<BookImageMetadata> findByBookIdOrderByDisplayOrderAscCreatedAtAsc(String bookId);

    List<BookImageMetadata> findByBookIdInOrderByBookIdAscDisplayOrderAscCreatedAtAsc(Collection<String> bookIds);

    Optional<BookImageMetadata> findByBookIdAndId(String bookId, String id);

    Optional<BookImageMetadata> findTopByBookIdOrderByDisplayOrderDesc(String bookId);

    long countByBookId(String bookId);

    void deleteByBookId(String bookId);

    void deleteByBookIdAndId(String bookId, String id);
}

