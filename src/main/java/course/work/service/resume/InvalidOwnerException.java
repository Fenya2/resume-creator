package course.work.service.resume;

public class InvalidOwnerException extends ResumeServiceException {
    public InvalidOwnerException() {
        super();
    }

    public InvalidOwnerException(String message) {
        super(message);
    }
}
