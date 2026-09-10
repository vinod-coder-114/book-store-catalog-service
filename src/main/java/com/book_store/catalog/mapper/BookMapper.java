package com.book_store.catalog.mapper;

import com.book_store.catalog.domain.Book;
import com.book_store.catalog.domain.BookImageMetadata;
import com.book_store.catalog.domain.BookImageVariant;
import com.book_store.catalog.domain.BookImages;
import com.book_store.catalog.domain.BookPricing;
import com.book_store.catalog.domain.BookRating;
import com.book_store.catalog.dto.BookDto;
import com.book_store.catalog.dto.BookImageDto;
import com.book_store.catalog.dto.BookUpsertRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class BookMapper {

    public BookDto toDto(Book entity) {
        return toDto(entity, List.of());
    }

    public BookDto toDto(Book entity, List<BookImageMetadata> imageMetadata) {
        if (entity == null) {
            return null;
        }

        BookDto dto = new BookDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setAuthor(entity.getAuthor());
        dto.setGenre(entity.getGenre());
        dto.setFormat(entity.getFormat());
        dto.setPricing(toDtoPricing(entity.getPricing()));
        dto.setRating(toDtoRating(entity.getRating()));
        dto.setImages(toDtoImages(entity.getId(), imageMetadata, entity.getImages()));
        return dto;
    }

    public Book toEntity(BookDto dto) {
        if (dto == null) {
            return null;
        }
        return toEntity(dto.getId(), dto.getTitle(), dto.getAuthor(), dto.getGenre(), dto.getFormat(), dto.getPricing(), dto.getRating());
    }

    public Book toEntity(BookUpsertRequest request) {
        if (request == null) {
            return null;
        }
        return toEntity(null, request.getTitle(), request.getAuthor(), request.getGenre(), request.getFormat(), request.getPricing(), request.getRating());
    }

    public List<BookDto> toDtoList(List<Book> entities) {
        return toDtoList(entities, Map.of());
    }

    public List<BookDto> toDtoList(List<Book> entities, Map<String, List<BookImageMetadata>> imagesByBookId) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<BookImageMetadata>> safeImageMap = imagesByBookId == null ? Map.of() : new LinkedHashMap<>(imagesByBookId);
        return entities.stream()
                .map(entity -> toDto(entity, safeImageMap.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    public List<Book> toEntityList(List<BookDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        return dtos.stream().map(this::toEntity).toList();
    }

    private Book toEntity(String id, String title, String author, String genre, String format, BookDto.Pricing pricing, BookDto.Rating rating) {
        Book entity = new Book();
        entity.setId(id);
        entity.setTitle(title);
        entity.setAuthor(author);
        entity.setGenre(genre);
        entity.setFormat(format);
        entity.setPricing(toEntityPricing(pricing));
        entity.setRating(toEntityRating(rating));
        entity.setImages(null);
        return entity;
    }

    private Book toEntity(String id, String title, String author, String genre, String format, BookUpsertRequest.Pricing pricing, BookUpsertRequest.Rating rating) {
        Book entity = new Book();
        entity.setId(id);
        entity.setTitle(title);
        entity.setAuthor(author);
        entity.setGenre(genre);
        entity.setFormat(format);
        entity.setPricing(toEntityPricing(pricing));
        entity.setRating(toEntityRating(rating));
        entity.setImages(null);
        return entity;
    }

    private BookDto.Pricing toDtoPricing(BookPricing pricing) {
        if (pricing == null) {
            return null;
        }
        BookDto.Pricing dtoPricing = new BookDto.Pricing();
        dtoPricing.setCurrency(pricing.getCurrency());
        dtoPricing.setSalePrice(pricing.getSalePrice());
        dtoPricing.setListPrice(pricing.getListPrice());
        return dtoPricing;
    }

    private BookPricing toEntityPricing(BookDto.Pricing pricing) {
        if (pricing == null) {
            return null;
        }
        BookPricing entityPricing = new BookPricing();
        entityPricing.setCurrency(pricing.getCurrency());
        entityPricing.setSalePrice(pricing.getSalePrice());
        entityPricing.setListPrice(pricing.getListPrice());
        return entityPricing;
    }

    private BookPricing toEntityPricing(BookUpsertRequest.Pricing pricing) {
        if (pricing == null) {
            return null;
        }
        BookPricing entityPricing = new BookPricing();
        entityPricing.setCurrency(pricing.getCurrency());
        entityPricing.setSalePrice(pricing.getSalePrice());
        entityPricing.setListPrice(pricing.getListPrice());
        return entityPricing;
    }

    private BookDto.Rating toDtoRating(BookRating rating) {
        if (rating == null) {
            return null;
        }
        BookDto.Rating dtoRating = new BookDto.Rating();
        dtoRating.setAverage(rating.getAverage());
        dtoRating.setCount(rating.getCount());
        return dtoRating;
    }

    private BookRating toEntityRating(BookDto.Rating rating) {
        if (rating == null) {
            return null;
        }
        BookRating entityRating = new BookRating();
        entityRating.setAverage(rating.getAverage());
        entityRating.setCount(rating.getCount());
        return entityRating;
    }

    private BookRating toEntityRating(BookUpsertRequest.Rating rating) {
        if (rating == null) {
            return null;
        }
        BookRating entityRating = new BookRating();
        entityRating.setAverage(rating.getAverage());
        entityRating.setCount(rating.getCount());
        return entityRating;
    }

    private List<BookImageDto> toDtoImages(String bookId, List<BookImageMetadata> metadataList, BookImages legacyImages) {
        if (metadataList != null && !metadataList.isEmpty()) {
            return metadataList.stream()
                    .map(metadata -> toImageDto(bookId, metadata))
                    .toList();
        }

        if (legacyImages == null) {
            return List.of();
        }

        return Stream.of(
                toLegacyImageDto("front", legacyImages.getFront(), 0, true),
                toLegacyImageDto("back", legacyImages.getBack(), 1, false)
        ).filter(image -> image != null && image.downloadUrl() != null).toList();
    }

    private BookImageDto toImageDto(String bookId, BookImageMetadata metadata) {
        return new BookImageDto(
                metadata.getId(),
                imageDownloadUrl(bookId, metadata.getId()),
                metadata.getOriginalFilename(),
                metadata.getContentType(),
                metadata.getSizeBytes(),
                metadata.getDisplayOrder(),
                metadata.isPrimaryImage(),
                metadata.getCreatedAt(),
                metadata.getUpdatedAt()
        );
    }

    private BookImageDto toLegacyImageDto(String fileName, BookImageVariant variant, int displayOrder, boolean primary) {
        if (variant == null) {
            return null;
        }

        String downloadUrl = variant.getCard() != null ? variant.getCard() : variant.getThumb();
        return new BookImageDto(
                null,
                downloadUrl,
                fileName,
                null,
                0L,
                displayOrder,
                primary,
                null,
                null
        );
    }

    private String imageDownloadUrl(String bookId, String imageId) {
        return "/catalog/books/%s/images/%s".formatted(bookId, imageId);
    }
}

