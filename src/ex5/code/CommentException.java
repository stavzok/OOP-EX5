package ex5.code;
/**
 * Represents an exception that occurs due to a comment-related issue.
 * This exception extends TypeOneException, meaning it leads to printing 1.
 *
 * @author inbar.el and stavzok
 */

public class CommentException extends TypeOneException{
    /**
     * Constructs a new CommentException with the specified detail message.
     *
     * @param message The detail message describing the exception.
     */

    public CommentException(String message) {
        super(message);
    }
}
