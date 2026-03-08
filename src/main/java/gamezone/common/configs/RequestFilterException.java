package gamezone.common.configs;

public class RequestFilterException extends Exception {

    private final int statusCode;

    public RequestFilterException() {
        super("Forbidden");
        this.statusCode = 403;
    }

    public RequestFilterException(String message) {
        super(message);
        this.statusCode = 403;
    }

    public RequestFilterException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
