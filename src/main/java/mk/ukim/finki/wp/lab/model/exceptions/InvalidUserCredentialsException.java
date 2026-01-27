package mk.ukim.finki.wp.lab.model.exceptions;

public class InvalidUserCredentialsException extends RuntimeException {
    public InvalidUserCredentialsException() {
        super("Invalid user credentials exception");
    }
}
