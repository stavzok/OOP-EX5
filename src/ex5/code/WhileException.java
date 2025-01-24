package ex5.code;

/**
 * Represents an exception that occurs due to an invalid while-statement.
 * This exception extends TypeOneException, meaning it leads to printing 1.
 *
 * @author inbar.el and stavzok
 */
public class WhileException extends TypeOneException{

    /**
     * Constructs a new WhileException with the specified detail message.
     *
     * @param message The detail message describing the exception.
     */
    public WhileException(String message) {
        super(message);
    }
}
