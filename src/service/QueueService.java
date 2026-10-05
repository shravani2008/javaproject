package service;

import dao.QueueDAO;
import dao.TokenDAO;
import model.QueueEntry;
import model.Token;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic for the Smart Queue Management System.
 *
 * This service acts as the middle layer between the UI
 * and the DAO/database layer.
 */
public class QueueService {

    private final QueueDAO queueDAO;
    private final TokenDAO tokenDAO;

    public QueueService() {
        this.queueDAO = new QueueDAO();
        this.tokenDAO = new TokenDAO();
    }

    /**
     * Adds a token to the active queue.
     */
    public void joinQueue(Token token) throws SQLException {

        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null.");
        }

        if (token.getTokenId() <= 0) {
            throw new IllegalArgumentException(
                    "Token must be saved before joining the queue.");
        }

        // Prevent duplicate active queue entries.
        QueueEntry existing = findQueueEntryByToken(token.getTokenId());

        if (existing != null) {
            throw new IllegalStateException(
                    "This token is already present in the queue.");
        }

        int waitingCount = queueDAO.countWaiting();

        double priority = calculatePriority(token);

        QueueEntry entry = new QueueEntry(
                token.getTokenId(),
                waitingCount + 1,
                priority
        );

        queueDAO.addToQueue(entry);

        tokenDAO.updateStatus(
                token.getTokenId(),
                Token.Status.WAITING
        );

        refreshQueue();
        updateEstimatedTimes();
    }

    /**
     * Calls the next customer according to queue priority.
     *
     * @return next waiting queue entry, or null if queue is empty
     */
    public QueueEntry callNext() throws SQLException {

        QueueEntry next = queueDAO.findNextWaiting();

        if (next == null) {
            return null;
        }

        boolean queueUpdated = queueDAO.updateStatus(
                next.getTokenId(),
                QueueEntry.Status.CALLED
        );

        boolean tokenUpdated = tokenDAO.markCalled(
                next.getTokenId()
        );

        if (!queueUpdated || !tokenUpdated) {
            throw new SQLException(
                    "Unable to call the next customer."
            );
        }

        refreshQueue();
        updateEstimatedTimes();

        return next;
    }

    /**
     * Starts serving a called customer.
     */
    public boolean startServing(long tokenId)
            throws SQLException {

        validateTokenId(tokenId);

        boolean queueUpdated = queueDAO.updateStatus(
                tokenId,
                QueueEntry.Status.SERVING
        );

        boolean tokenUpdated = tokenDAO.markServing(tokenId);

        return queueUpdated && tokenUpdated;
    }

    /**
     * Completes service for a customer.
     */
    public boolean completeService(long tokenId)
            throws SQLException {

        validateTokenId(tokenId);

        boolean tokenUpdated = tokenDAO.markCompleted(tokenId);

        boolean queueUpdated = queueDAO.updateStatus(
                tokenId,
                QueueEntry.Status.COMPLETED
        );

        if (tokenUpdated && queueUpdated) {
            refreshQueue();
            updateEstimatedTimes();
        }

        return tokenUpdated && queueUpdated;
    }

    /**
     * Skips a customer.
     */
    public boolean skipCustomer(long tokenId)
            throws SQLException {

        validateTokenId(tokenId);

        boolean queueUpdated = queueDAO.updateStatus(
                tokenId,
                QueueEntry.Status.SKIPPED
        );

        boolean tokenUpdated = tokenDAO.updateStatus(
                tokenId,
                Token.Status.SKIPPED
        );

        if (queueUpdated && tokenUpdated) {
            refreshQueue();
            updateEstimatedTimes();
        }

        return queueUpdated && tokenUpdated;
    }

    /**
     * Cancels a customer's queue entry.
     */
    public boolean cancelQueue(long tokenId)
            throws SQLException {

        validateTokenId(tokenId);

        boolean queueUpdated = queueDAO.updateStatus(
                tokenId,
                QueueEntry.Status.CANCELLED
        );

        boolean tokenUpdated = tokenDAO.updateStatus(
                tokenId,
                Token.Status.CANCELLED
        );

        if (queueUpdated && tokenUpdated) {
            refreshQueue();
            updateEstimatedTimes();
        }

        return queueUpdated && tokenUpdated;
    }

    /**
     * Returns the number of customers currently waiting.
     */
    public int getWaitingCount() throws SQLException {
        return queueDAO.countWaiting();
    }

    /**
     * Returns all currently waiting customers.
     */
    public List<QueueEntry> getWaitingQueue()
            throws SQLException {

        return queueDAO.findWaitingEntries();
    }

    /**
     * Returns the current queue position of a token.
     *
     * @return position, or -1 if token is not waiting
     */
    public int getCustomerPosition(long tokenId)
            throws SQLException {

        validateTokenId(tokenId);

        return queueDAO.getPosition(tokenId);
    }

    /**
     * Finds a queue entry using its token ID.
     */
    public QueueEntry findQueueEntryByToken(long tokenId)
            throws SQLException {

        validateTokenId(tokenId);

        List<QueueEntry> entries =
                queueDAO.findWaitingEntries();

        for (QueueEntry entry : entries) {
            if (entry.getTokenId() == tokenId) {
                return entry;
            }
        }

        return null;
    }

    /**
     * Refreshes queue positions.
     */
    public void refreshQueue() throws SQLException {
        queueDAO.refreshPositions();
    }

    /**
     * Updates estimated waiting time for all waiting customers.
     *
     * The current system uses an average estimate of
     * 5 minutes per customer ahead in the queue.
     */
    public void updateEstimatedTimes()
            throws SQLException {

        List<QueueEntry> entries =
                queueDAO.findWaitingEntries();

        for (int i = 0; i < entries.size(); i++) {

            double estimatedMinutes = i * 5.0;

            tokenDAO.updateEstimatedWait(
                    entries.get(i).getTokenId(),
                    estimatedMinutes
            );
        }
    }

    /**
     * Returns the estimated waiting time for a token.
     *
     * @return estimated minutes, or -1 if token is not found
     */
    public double getEstimatedWait(long tokenId)
            throws SQLException {

        validateTokenId(tokenId);

        Token token = tokenDAO.findById(tokenId);

        if (token == null ||
                token.getEstimatedWaitMinutes() == null) {
            return -1;
        }

        return token.getEstimatedWaitMinutes();
    }

    /**
     * Returns a token by its ID.
     */
    public Token getToken(long tokenId)
            throws SQLException {

        validateTokenId(tokenId);

        return tokenDAO.findById(tokenId);
    }

    /**
     * Returns a token using its token code.
     */
    public Token getTokenByCode(String tokenCode)
            throws SQLException {

        if (tokenCode == null || tokenCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Token code cannot be empty."
            );
        }

        return tokenDAO.findByCode(tokenCode);
    }

    /**
     * Generates the next token code.
     */
    public String generateNextTokenCode()
            throws SQLException {

        return tokenDAO.generateNextTokenCode();
    }

    /**
     * Calculates queue priority.
     *
     * Higher value = higher priority.
     */
    private double calculatePriority(Token token) {

        if (token == null ||
                token.getPriorityLevel() == null) {
            return 10.0;
        }

        return switch (token.getPriorityLevel()) {

            case APPOINTMENT -> 30.0;

            case PRIORITY -> 20.0;

            case NORMAL -> 10.0;
        };
    }

    /**
     * Validates a token ID before database operations.
     */
    private void validateTokenId(long tokenId) {

        if (tokenId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid token ID."
            );
        }
    }
}