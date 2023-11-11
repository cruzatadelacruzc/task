package aleph.engineering.note.web.error;

public class ItemNotFoundException extends RuntimeException {
    
    public ItemNotFoundException(String message) {
        super(message);
    }
}
