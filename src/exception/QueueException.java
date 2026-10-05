package exception;

/**
 * Custom checked exception used throughout the Smart Queue
 * Management and Waiting-Time Optimization System.
 *
 * This exception represents business-level problems such as:
 * - Invalid queue operations
 * - Invalid token transitions
 * - Database-related application errors
 * - Invalid service/counter operations
 */
public class QueueException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a message.
     *
     * @param message descriptive error message
     */
    public QueueException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a message and underlying cause.
     *
     * @param message descriptive error message
     * @param cause original exception
     */
    public QueueException(String message, Throwable cause) {
        super(message, cause);
    }
}