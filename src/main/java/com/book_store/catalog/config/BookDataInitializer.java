package com.book_store.catalog.config;

import com.book_store.catalog.domain.Book;
import com.book_store.catalog.domain.BookImages;
import com.book_store.catalog.domain.BookImageVariant;
import com.book_store.catalog.domain.BookPricing;
import com.book_store.catalog.domain.BookRating;
import com.book_store.catalog.repository.BookRepository;
import org.slf4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
public class BookDataInitializer implements ApplicationRunner {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BookDataInitializer.class);

    private final BookRepository bookRepository;

    public BookDataInitializer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (bookRepository.count() > 0) {
            return;
        }

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
}

