package ex5.code;

/**
 * Represents a general exception for all errors that should lead to printing 1.
 * This class extends Exception and serves as a base class for such errors.
 *
 * @author inbar.el and stavzok
 */
public class TypeOneException extends Exception {
    /**
     * Constructs a new TypeOneException with the specified detail message.
     *
     * @param message The detail message describing the exception.
     */
    public TypeOneException(String message) {
        super(message);
    }
}
