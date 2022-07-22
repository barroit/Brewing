package com.baioretto.brewing.exception;

@SuppressWarnings("unused")
public class BrewingInternalException extends RuntimeException {
    public BrewingInternalException() {
        super("This exception is the brewing plugin internal exception. You can ignore this exception or submit an issue in my GitHub repository.");
    }

    public BrewingInternalException(Throwable cause) {
        super("This exception is the brewing plugin internal exception. You can ignore this exception or submit an issue in my GitHub repository. The error is: " + cause.toString());
    }

    public BrewingInternalException(String message) {
        super(message);
    }
}
