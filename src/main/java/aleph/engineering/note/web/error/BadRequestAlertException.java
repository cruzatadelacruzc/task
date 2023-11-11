package aleph.engineering.note.web.error;

public class BadRequestAlertException extends RuntimeException {
    private final String errorKey;

    public BadRequestAlertException(String defaultMessage, String errorKey) {
        super(defaultMessage);
        this.errorKey = errorKey;
    }

    public String getErrorKey() {
        return errorKey;
    }
    
}
