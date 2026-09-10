package com.book_store.catalog.service;

import com.book_store.catalog.config.MinioProperties;
import com.book_store.catalog.exception.ResourceNotFoundException;
import com.book_store.catalog.exception.StorageException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class MinioObjectStorageService implements ObjectStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public MinioObjectStorageService(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Override
    public void putObject(String objectKey, byte[] content, String contentType) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
            ensureBucketExists();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.bucket())
                    .object(objectKey)
                    .stream(inputStream, content.length, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            throw new StorageException("Failed to upload image to object storage", e);
        }
    }

    @Override
    public ImageObject getObject(String objectKey) {
        try {
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioProperties.bucket())
                    .object(objectKey)
                    .build());
            byte[] content;
            try (var stream = minioClient.getObject(GetObjectArgs.builder()
                            .bucket(minioProperties.bucket())
                            .object(objectKey)
                            .build())) {
                content = stream.readAllBytes();
            }

            return new ImageObject(content, stat.contentType(), stat.size(), stat.etag());
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equalsIgnoreCase(e.errorResponse().code())) {
                throw new ResourceNotFoundException("Image not found in object storage");
            }
            throw new StorageException("Failed to read image from object storage", e);
        } catch (Exception e) {
            throw new StorageException("Failed to read image from object storage", e);
        }
    }

    @Override
    public void deleteObject(String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.bucket())
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            throw new StorageException("Failed to delete image from object storage", e);
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.bucket()).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.bucket()).build());
        }
    }
}

