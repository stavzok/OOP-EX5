package ex5.code;

/**
 * Represents an exception that occurs due to an invalid if-statement.
 * This exception extends TypeOneException, meaning it leads to printing 1.
 *
 * @author inbar.el and stavzok
 */
public class IfException extends TypeOneException{
    /**
     * Constructs a new IfException with the specified detail message.
     *
     * @param message The detail message describing the exception.
     */
    public IfException(String message) {
        super(message);
    }
}
