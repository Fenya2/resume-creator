package course.work.s3;

public class PhotoNotFoundException extends S3Exception {
    public PhotoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
