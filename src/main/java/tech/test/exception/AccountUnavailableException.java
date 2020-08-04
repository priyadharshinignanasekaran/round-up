package tech.test.exception;

public class AccountUnavailableException extends RuntimeException {

    public AccountUnavailableException(String message) {
        super(message);
    }

}
