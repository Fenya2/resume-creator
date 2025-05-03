package course.work.service.exporting;

public class ExportingException extends RuntimeException {
    public ExportingException(String message) {
        super(message);
    }

    public ExportingException(String message, Throwable cause) {
        super(message, cause);
    }
}
