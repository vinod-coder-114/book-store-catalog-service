package com.book_store.catalog.config;

import com.book_store.catalog.domain.Book;
import com.book_store.catalog.domain.BookImageMetadata;
import com.book_store.catalog.domain.BookImages;
import com.book_store.catalog.domain.BookImageVariant;
import com.book_store.catalog.domain.BookPricing;
import com.book_store.catalog.domain.BookRating;
import com.book_store.catalog.repository.BookImageMetadataRepository;
import com.book_store.catalog.repository.BookRepository;
import org.slf4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

//@Component
@ConditionalOnProperty(name = "catalog.seed.enabled", havingValue = "true", matchIfMissing = true)
public class BookDataInitializer implements ApplicationRunner {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BookDataInitializer.class);

    private final BookRepository bookRepository;
    private final BookImageMetadataRepository bookImageMetadataRepository;

    public BookDataInitializer(BookRepository bookRepository, BookImageMetadataRepository bookImageMetadataRepository) {
        this.bookRepository = bookRepository;
        this.bookImageMetadataRepository = bookImageMetadataRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (bookRepository.count() == 0) {
            List<Book> seedBooks = List.of(
                    createBook("bk_10293", "Atomic Habits", "James Clear", "Self Help", "Paperback", 399, 499, 4.7, 12933),
                    createBook("bk_10411", "Deep Work", "Cal Newport", "Productivity", "Hardcover", 549, 699, 4.6, 8451),
                    createBook("bk_10754", "The Psychology of Money", "Morgan Housel", "Finance", "Paperback", 379, 450, 4.8, 21445),
                    createBook("bk_10902", "Clean Code", "Robert C. Martin", "Programming", "Paperback", 699, 899, 4.5, 17602),
                    createBook("bk_11037", "Ikigai", "Hector Garcia", "Wellness", "Paperback", 299, 399, 4.4, 9820)
            );

            bookRepository.saveAll(seedBooks);
            logger.info("Seeded {} books into MongoDB", seedBooks.size());
        }

        if (bookImageMetadataRepository.count() == 0) {
            List<BookImageMetadata> seedImages = List.of(
                    createImage("img_bk_10293_front", "bk_10293", "atomic-habits-front.avif", 0, true),
                    createImage("img_bk_10293_back", "bk_10293", "atomic-habits-back.avif", 1, false),
                    createImage("img_bk_10411_front", "bk_10411", "deep-work-front.avif", 0, true),
                    createImage("img_bk_10411_back", "bk_10411", "deep-work-back.avif", 1, false),
                    createImage("img_bk_10754_front", "bk_10754", "psychology-of-money-front.avif", 0, true),
                    createImage("img_bk_10754_back", "bk_10754", "psychology-of-money-back.avif", 1, false),
                    createImage("img_bk_10902_front", "bk_10902", "clean-code-front.avif", 0, true),
                    createImage("img_bk_10902_back", "bk_10902", "clean-code-back.avif", 1, false),
                    createImage("img_bk_11037_front", "bk_11037", "ikigai-front.avif", 0, true),
                    createImage("img_bk_11037_back", "bk_11037", "ikigai-back.avif", 1, false)
            );

            bookImageMetadataRepository.saveAll(seedImages);
            logger.info("Seeded {} book_images records into MongoDB", seedImages.size());
        }
    }

    private Book createBook(String id, String title, String author, String genre, String format,
                           int salePrice, int listPrice, double averageRating, int reviewCount) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setFormat(format);

        BookPricing pricing = new BookPricing();
        pricing.setCurrency("INR");
        pricing.setSalePrice(salePrice);
        pricing.setListPrice(listPrice);
        book.setPricing(pricing);

        BookRating rating = new BookRating();
        rating.setAverage(averageRating);
        rating.setCount(reviewCount);
        book.setRating(rating);

        BookImages images = new BookImages();
        BookImageVariant front = new BookImageVariant();
        front.setCard("https://cdn.example.com/books/" + id + "/front-card.avif");
        front.setThumb("https://cdn.example.com/books/" + id + "/front-thumb.avif");

        BookImageVariant back = new BookImageVariant();
        back.setCard("https://cdn.example.com/books/" + id + "/back-card.avif");
        back.setThumb("https://cdn.example.com/books/" + id + "/back-thumb.avif");

        images.setFront(front);
        images.setBack(back);
        images.setAspectRatio(0.75);
        images.setBlurHash("LKO2?U%2Tw=w]~RBVZRi};RPxuwH");
        book.setImages(images);

        return book;
    }

    private BookImageMetadata createImage(String imageId, String bookId, String fileName, int displayOrder, boolean primary) {
        BookImageMetadata image = new BookImageMetadata();
        image.setId(imageId);
        image.setBookId(bookId);
        image.setObjectKey("books/%s/%d/%s".formatted(bookId, displayOrder, fileName));
        image.setOriginalFilename(fileName);
        image.setContentType("image/avif");
        image.setSizeBytes(0L);
        image.setDisplayOrder(displayOrder);
        image.setPrimaryImage(primary);
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        image.setCreatedAt(createdAt);
        image.setUpdatedAt(createdAt);
        return image;
    }
}

