package com.byborg.exception;

public class TechnicalException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public TechnicalException(String message) {
        super(message);
    }

    public TechnicalException(Throwable cause) {
        super(cause);
    }
}
