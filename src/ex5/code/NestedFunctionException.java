package ex5.code;

/**
 * Represents an exception that occurs when there is a trial to create
 * a function that's nested inside another function, which is illegal in Sjava.
 * This exception extends TypeOneException, meaning it leads to printing 1.
 *
 * @author inbar.el and stavzok
 */
public class NestedFunctionException extends TypeOneException{
    /**
     * Constructs a new NestedFunctionException with the specified detail message.
     *
     * @param message The detail message describing the exception.
     */
    public NestedFunctionException(String message) {
        super(message);
    }
}
