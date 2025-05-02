package course.work.s3;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dsyromyatnikov
 * @since 01.05.2025
 */
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String photoBucket;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        createPhotoBucket(client);
        return client;
    }

    private void createPhotoBucket(MinioClient client) {
        try {
            boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(photoBucket).build());
            if (!found) {
                client.makeBucket(MakeBucketArgs.builder().bucket(photoBucket).build());
            }
        } catch (Exception e) {
            throw new S3Exception("Can't create photo bucket", e);
        }
    }
}