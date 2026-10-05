package service;

import dao.TokenDAO;
import model.Token;

import java.sql.SQLException;
import java.util.List;

/**

* Provides analytics and reporting operations for the
* Smart Queue Management System.
*
* <p>
* This service layer retrieves token-related information
* from TokenDAO and provides meaningful statistics to
* the application/UI layer.
* </p>

*/
public class AnalyticsService {


private final TokenDAO tokenDAO;

/**
 * Creates an AnalyticsService using the default TokenDAO.
 */
public AnalyticsService() {
    this.tokenDAO = new TokenDAO();
}

/**
 * Returns the total number of tokens.
 *
 * @return total token count
 * @throws SQLException if database operation fails
 */
public long getTotalTokens() throws SQLException {
    long total = 0;

    for (Token.Status status : Token.Status.values()) {
        total += tokenDAO.countByStatus(status);
    }

    return total;
}

/**
 * Returns the number of waiting tokens.
 *
 * @return waiting token count
 * @throws SQLException if database operation fails
 */
public long getWaitingTokens() throws SQLException {
    return tokenDAO.countByStatus(Token.Status.WAITING);
}

/**
 * Returns the number of called tokens.
 *
 * @return called token count
 * @throws SQLException if database operation fails
 */
public long getCalledTokens() throws SQLException {
    return tokenDAO.countByStatus(Token.Status.CALLED);
}

/**
 * Returns the number of currently serving tokens.
 *
 * @return serving token count
 * @throws SQLException if database operation fails
 */
public long getServingTokens() throws SQLException {
    return tokenDAO.countByStatus(Token.Status.SERVING);
}

/**
 * Returns the number of completed tokens.
 *
 * @return completed token count
 * @throws SQLException if database operation fails
 */
public long getCompletedTokens() throws SQLException {
    return tokenDAO.countByStatus(Token.Status.COMPLETED);
}

/**
 * Returns the number of cancelled tokens.
 *
 * @return cancelled token count
 * @throws SQLException if database operation fails
 */
public long getCancelledTokens() throws SQLException {
    return tokenDAO.countByStatus(Token.Status.CANCELLED);
}

/**
 * Returns the number of skipped tokens.
 *
 * @return skipped token count
 * @throws SQLException if database operation fails
 */
public long getSkippedTokens() throws SQLException {
    return tokenDAO.countByStatus(Token.Status.SKIPPED);
}

/**
 * Calculates the completion rate.
 *
 * @return completion percentage
 * @throws SQLException if database operation fails
 */
public double getCompletionRate() throws SQLException {

    long total = getTotalTokens();

    if (total == 0) {
        return 0.0;
    }

    long completed = getCompletedTokens();

    return (completed * 100.0) / total;
}

/**
 * Calculates the cancellation rate.
 *
 * @return cancellation percentage
 * @throws SQLException if database operation fails
 */
public double getCancellationRate() throws SQLException {

    long total = getTotalTokens();

    if (total == 0) {
        return 0.0;
    }

    long cancelled = getCancelledTokens();

    return (cancelled * 100.0) / total;
}

/**
 * Returns all tokens belonging to a customer.
 *
 * @param customerId customer ID
 * @return customer's token list
 * @throws SQLException if database operation fails
 */
public List<Token> getCustomerTokens(
        int customerId
) throws SQLException {

    if (customerId <= 0) {
        throw new IllegalArgumentException(
                "Customer ID must be greater than zero."
        );
    }

    return tokenDAO.findByCustomerId(customerId);
}

/**
 * Returns active tokens for a particular service.
 *
 * @param serviceId service ID
 * @return active service tokens
 * @throws SQLException if database operation fails
 */
public List<Token> getActiveTokensByService(
        int serviceId
) throws SQLException {

    if (serviceId <= 0) {
        throw new IllegalArgumentException(
                "Service ID must be greater than zero."
        );
    }

    return tokenDAO.findActiveByServiceId(serviceId);
}

/**
 * Returns a formatted analytics summary.
 *
 * @return analytics summary text
 * @throws SQLException if database operation fails
 */
public String getSummary() throws SQLException {

    return String.format(
            """
            Smart Queue Analytics

            Total Tokens     : %d
            Waiting Tokens   : %d
            Called Tokens    : %d
            Serving Tokens   : %d
            Completed Tokens : %d
            Cancelled Tokens : %d
            Skipped Tokens   : %d

            Completion Rate  : %.2f%%
            Cancellation Rate: %.2f%%
            """,
            getTotalTokens(),
            getWaitingTokens(),
            getCalledTokens(),
            getServingTokens(),
            getCompletedTokens(),
            getCancelledTokens(),
            getSkippedTokens(),
            getCompletionRate(),
            getCancellationRate()
    );
}}
