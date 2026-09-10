package com.book_store.catalog.service;

import com.book_store.catalog.domain.Book;
import com.book_store.catalog.domain.BookImageMetadata;
import com.book_store.catalog.dto.BookDto;
import com.book_store.catalog.dto.BookUpsertRequest;
import com.book_store.catalog.exception.ResourceNotFoundException;
import com.book_store.catalog.mapper.BookMapper;
import com.book_store.catalog.repository.BookRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookImageService bookImageService;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BookService.class);

    public BookService(BookRepository bookRepository, BookMapper bookMapper, BookImageService bookImageService) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.bookImageService = bookImageService;
    }

    public List<BookDto> getAllBooks() {
        logger.info("Fetching all books from the repository");
        List<Book> allBooks = bookRepository.findAll();
        logger.info("Total books fetched: {}", allBooks.size());
        Map<String, List<BookImageMetadata>> imagesByBookId = bookImageService.findImagesByBookIds(
                allBooks.stream().map(Book::getId).collect(Collectors.toList())
        );
        return bookMapper.toDtoList(allBooks, imagesByBookId);
    }

    public BookDto getBookById(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        return bookMapper.toDto(book, bookImageService.listImages(bookId));
    }

    public BookDto createBook(BookDto bookDto) {
        Book saved = bookRepository.save(bookMapper.toEntity(bookDto));
        return bookMapper.toDto(saved, List.of());
    }

    public BookDto createBook(BookUpsertRequest request, List<MultipartFile> images) {
        return createBook(bookMapper.toEntity(request), images);
    }

    public BookDto updateBook(String bookId, BookDto bookDto) {
        return updateBook(bookId, bookMapper.toEntity(bookDto));
    }

    public BookDto updateBook(String bookId, BookUpsertRequest request) {
        return updateBook(bookId, bookMapper.toEntity(request));
    }

    public void deleteBook(String bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found with id: " + bookId);
        }
        bookImageService.deleteAllImagesForBook(bookId);
        bookRepository.deleteById(bookId);
    }

    private BookDto createBook(Book book, List<MultipartFile> images) {
        Book saved = bookRepository.save(book);
        try {
            List<BookImageMetadata> uploadedImages = images == null || images.isEmpty()
                    ? List.of()
                    : bookImageService.uploadImages(saved.getId(), images);
            return bookMapper.toDto(saved, uploadedImages);
        } catch (RuntimeException ex) {
            cleanupBook(saved.getId());
            throw ex;
        }
    }

    private BookDto updateBook(String bookId, Book book) {
        Book existing = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        Book toSave = book;
        toSave.setId(existing.getId());

        Book updated = bookRepository.save(toSave);
        return bookMapper.toDto(updated, bookImageService.listImages(bookId));
    }

    private void cleanupBook(String bookId) {
        try {
            bookImageService.deleteAllImagesForBook(bookId);
        } catch (RuntimeException ex) {
            logger.warn("Failed to cleanup images for book {} after a create failure", bookId, ex);
        }
        if (bookRepository.existsById(bookId)) {
            bookRepository.deleteById(bookId);
        }
    }
}

