package ex5.code;
/**
 * Represents an exception that occurs due to an invalid function call.
 * This exception extends TypeOneException, meaning it leads to printing 1.
 *
 * @author inbar.el and stavzok
 */
public class FunctionCallException extends TypeOneException{
    /**
     * Constructs a new FunctionCallException with the specified detail message.
     *
     * @param message The detail message describing the exception.
     */
    public FunctionCallException(String message) {
        super(message);
    }
}
