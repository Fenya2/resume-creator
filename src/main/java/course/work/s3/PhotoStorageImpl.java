package course.work.s3;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author dsyromyatnikov
 * @since 01.05.2025
 */
@Service
public class PhotoStorageImpl implements PhotoStorage {
    public static final Logger LOG = LoggerFactory.getLogger(PhotoStorageImpl.class);

    private final MinioClient client;
    private final String photoBucket;

    @Autowired
    public PhotoStorageImpl(MinioClient minioClient,
                            @Value("${minio.bucket}") String photoBucket) {
        this.client = minioClient;
        this.photoBucket = photoBucket;
    }

    @Override
    public PhotoUUID uploadPhoto(Photo photo) {
        UUID uuid = UUID.randomUUID();
        byte[] data = photo.data();

        try {
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(photoBucket)
                            .object(uuid.toString())
                            .stream(new ByteArrayInputStream(data), data.length, -1)
                            .contentType(MimeTypeUtils.IMAGE_JPEG_VALUE)
                            .build()
            );
        } catch (Exception e) {
            throw new UnavailableS3StorageException("Can't put photo", e);
        }

        LOG.atInfo().log("Photo {} uploaded", uuid);
        return new PhotoUUID(uuid);
    }

    @Override
    public Photo getPhoto(PhotoUUID photoUUID) {
        try (InputStream stream = client.getObject(
                GetObjectArgs.builder()
                        .bucket(photoBucket)
                        .object(photoUUID.photoId().toString())
                        .build()
        )) {
            byte[] imageData = stream.readAllBytes();
            return new Photo(imageData);
        } catch (Exception e) {
            throw new UnavailableS3StorageException("Failed to get photo with UUID: " + photoUUID, e);
        }
    }

    @Override
    public void deletePhoto(PhotoUUID photoUUID) {
        try {
            client.statObject(
                    StatObjectArgs.builder()
                            .bucket(photoBucket)
                            .object(photoUUID.photoId().toString())
                            .build());

            client.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(photoBucket)
                            .object(photoUUID.photoId().toString())
                            .build());
            LOG.atInfo().log("Successfully deleted photo with UUID: {}", photoUUID);
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                throw new PhotoNotFoundException("Photo not found with UUID: " + photoUUID, e);
            }
            throw new UnavailableS3StorageException("Failed to delete photo with UUID: " + photoUUID, e);
        } catch (Exception e) {
            throw new UnavailableS3StorageException("Failed to delete photo with UUID: " + photoUUID, e);
        }
    }
}
