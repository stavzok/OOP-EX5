package ex5.code;


/**
 * Represents an exception that occurs due to an invalid return statement.
 * This exception extends TypeOneException, meaning it leads to printing 1.
 *
 * @author inbar.el and stavzok
 */

public class ReturnException extends TypeOneException {
    /**
     * Constructs a new ReturnException with the specified detail message.
     *
     * @param message The detail message describing the exception.
     */
    public ReturnException(String message) {
        super(message);
    }
}
