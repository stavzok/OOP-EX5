package ex5.code;

/**
 * Represents an exception that occurs due to an invalid function declaration.
 * This exception extends TypeOneException, meaning it leads to printing 1.
 *
 * @author inbar.el and stavzok
 */
public class FunctionDeclarationException extends TypeOneException{

    /**
     * Constructs a new FunctionDeclarationException with the specified detail message.
     *
     * @param message The detail message describing the exception.
     */
    public FunctionDeclarationException(String message) {
        super(message);
    }
}
