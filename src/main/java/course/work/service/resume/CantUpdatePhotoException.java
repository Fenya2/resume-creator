package course.work.service.resume;

/**
 * @author dsyromyatnikov
 * @since 01.05.2025
 */
public class CantUpdatePhotoException extends RuntimeException {
    public CantUpdatePhotoException(String message, Throwable cause) {
        super(message, cause);
    }
}
