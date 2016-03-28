package Google;

public class GoogleSearchException extends Exception {

    public GoogleSearchException() {
        super("Not found.");
    }

    public GoogleSearchException(String message) {
        super(message);
    }
}
