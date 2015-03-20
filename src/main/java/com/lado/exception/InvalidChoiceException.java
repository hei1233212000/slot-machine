package com.lado.exception;

public class InvalidChoiceException extends RuntimeException {
    public InvalidChoiceException() { super(); }

    public InvalidChoiceException(String s) { super(s); }

    public InvalidChoiceException(String s, Throwable throwable) { super(s, throwable); }

    public InvalidChoiceException(Throwable throwable) { super(throwable); }
}
