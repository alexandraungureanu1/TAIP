package ro.uaic.info.aset.gateway.exceptions;

public class TokenParsingException extends RuntimeException {
    public TokenParsingException(Throwable cause) {
        super(cause);
    }

    public TokenParsingException(String message) {
        super(message);
    }
}
