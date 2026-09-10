package com.book_store.catalog.service;

import com.book_store.catalog.config.CatalogImageProperties;
import com.book_store.catalog.domain.BookImageMetadata;
import com.book_store.catalog.exception.BadRequestException;
import com.book_store.catalog.exception.ResourceNotFoundException;
import com.book_store.catalog.repository.BookImageMetadataRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookImageService {

    private final BookImageMetadataRepository metadataRepository;
    private final ObjectStorageService objectStorageService;
    private final CatalogImageProperties imageProperties;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BookImageService.class);

    public BookImageService(
            BookImageMetadataRepository metadataRepository,
            ObjectStorageService objectStorageService,
            CatalogImageProperties imageProperties
    ) {
        this.metadataRepository = metadataRepository;
        this.objectStorageService = objectStorageService;
        this.imageProperties = imageProperties;
    }

    public List<BookImageMetadata> uploadImages(String bookId, List<MultipartFile> files) {
        validateUpload(bookId, files);

        int nextDisplayOrder = metadataRepository.findTopByBookIdOrderByDisplayOrderDesc(bookId)
                .map(BookImageMetadata::getDisplayOrder)
                .map(value -> value + 1)
                .orElse(0);

        List<BookImageMetadata> savedMetadata = new ArrayList<>();
        List<String> uploadedObjectKeys = new ArrayList<>();

        try {
            for (int index = 0; index < files.size(); index++) {
                MultipartFile file = files.get(index);
                byte[] content = readBytes(file);
                int displayOrder = nextDisplayOrder + index;
                String objectKey = buildObjectKey(bookId, displayOrder, file.getOriginalFilename());

                objectStorageService.putObject(objectKey, content, file.getContentType());
                uploadedObjectKeys.add(objectKey);

                Instant now = Instant.now();
                BookImageMetadata metadata = new BookImageMetadata();
                metadata.setBookId(bookId);
                metadata.setObjectKey(objectKey);
                metadata.setOriginalFilename(file.getOriginalFilename());
                metadata.setContentType(file.getContentType());
                metadata.setSizeBytes(content.length);
                metadata.setDisplayOrder(displayOrder);
                metadata.setPrimaryImage(displayOrder == 0);
                metadata.setCreatedAt(now);
                metadata.setUpdatedAt(now);

                savedMetadata.add(metadataRepository.save(metadata));
            }

            return savedMetadata;
        } catch (RuntimeException ex) {
            rollbackUploadedArtifacts(savedMetadata, uploadedObjectKeys);
            throw ex;
        } catch (Exception ex) {
            rollbackUploadedArtifacts(savedMetadata, uploadedObjectKeys);
            throw new BadRequestException("Invalid image payload");
        }
    }

    public Map<String, List<BookImageMetadata>> findImagesByBookIds(Collection<String> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return Map.of();
        }

        List<BookImageMetadata> allImages = metadataRepository.findByBookIdInOrderByBookIdAscDisplayOrderAscCreatedAtAsc(bookIds);
        if (allImages.isEmpty()) {
            return Map.of();
        }

        return allImages.stream().collect(Collectors.groupingBy(
                BookImageMetadata::getBookId,
                LinkedHashMap::new,
                Collectors.toList()
        ));
    }

    public List<BookImageMetadata> listImages(String bookId) {
        return metadataRepository.findByBookIdOrderByDisplayOrderAscCreatedAtAsc(bookId);
    }

    public ImageObject getImage(String bookId, String imageId) {
        BookImageMetadata metadata = metadataRepository.findByBookIdAndId(bookId, imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image metadata not found"));
        return objectStorageService.getObject(metadata.getObjectKey());
    }

    public void deleteImage(String bookId, String imageId) {
        BookImageMetadata metadata = metadataRepository.findByBookIdAndId(bookId, imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image metadata not found"));

        objectStorageService.deleteObject(metadata.getObjectKey());
        metadataRepository.deleteByBookIdAndId(bookId, imageId);
    }

    public void deleteAllImagesForBook(String bookId) {
        List<BookImageMetadata> images = listImages(bookId);
        for (BookImageMetadata metadata : images) {
            try {
                objectStorageService.deleteObject(metadata.getObjectKey());
            } catch (RuntimeException ex) {
                logger.warn("Failed to delete image object {} while deleting book {}", metadata.getObjectKey(), bookId, ex);
            }
        }
        metadataRepository.deleteByBookId(bookId);
    }

    private void validateUpload(String bookId, List<MultipartFile> files) {
        if (!StringUtils.hasText(bookId)) {
            throw new BadRequestException("bookId is required");
        }

        if (files == null || files.isEmpty()) {
            throw new BadRequestException("At least one image file is required");
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new BadRequestException("Image file is required");
            }

            if (file.getSize() > imageProperties.maxSizeBytes()) {
                throw new BadRequestException("Image exceeds max configured size");
            }

            String contentType = file.getContentType();
            if (!StringUtils.hasText(contentType)) {
                throw new BadRequestException("Content type is required");
            }

            boolean allowed = imageProperties.allowedContentTypes().stream()
                    .map(v -> v.toLowerCase(Locale.ROOT))
                    .anyMatch(v -> v.equals(contentType.toLowerCase(Locale.ROOT)));

            if (!allowed) {
                throw new BadRequestException("Unsupported content type: " + contentType);
            }
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new BadRequestException("Invalid image payload");
        }
    }

    private void rollbackUploadedArtifacts(List<BookImageMetadata> savedMetadata, List<String> uploadedObjectKeys) {
        for (String objectKey : uploadedObjectKeys) {
            try {
                objectStorageService.deleteObject(objectKey);
            } catch (RuntimeException ex) {
                logger.warn("Failed to rollback uploaded object {}", objectKey, ex);
            }
        }

        for (BookImageMetadata metadata : savedMetadata) {
            try {
                metadataRepository.deleteById(metadata.getId());
            } catch (RuntimeException ex) {
                logger.warn("Failed to rollback metadata {}", metadata.getId(), ex);
            }
        }
    }

    private String buildObjectKey(String bookId, int displayOrder, String originalFilename) {
        String extension = "bin";
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        }

        return "books/%s/%s/%s.%s".formatted(
                bookId,
                displayOrder,
                UUID.randomUUID(),
                extension
        );
    }
}

