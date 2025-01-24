package ex5.code;
/**
 * Represents an exception that occurs due to an invalid variable declaration or usage.
 * This exception extends TypeOneException, meaning it leads to printing 1.
 *
 * @author inbar.el and stavzok
 */
public class VariablesException extends TypeOneException{
    /**
     * Constructs a new VariablesException with the specified detail message.
     *
     * @param message The detail message describing the exception.
     */
    public VariablesException(String message) {
        super(message);
    }
}
