package exception;

/**
 * Custom unchecked exception used for database-related failures
 * in the Smart Queue Management System.
 *
 * <p>This exception provides a consistent way to propagate database
 * errors from the DAO/database layer to the service or UI layer.</p>
 */
public class DatabaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a database exception with a descriptive message.
     *
     * @param message description of the database error
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Creates a database exception with a message and original cause.
     *
     * @param message description of the database error
     * @param cause original exception that caused the failure
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a database exception with the original cause.
     *
     * @param cause original exception that caused the failure
     */
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}