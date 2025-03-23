package course.work.s3;

/**
 * @author dsyromyatnikov
 * @since 01.05.2025
 */
public class UnavailableS3StorageException extends S3Exception {
    public UnavailableS3StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
