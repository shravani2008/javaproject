package service;

import model.QueueEntry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Service responsible for calculating customer waiting time.
 *
 * <p>
 * WaitingTimeService contains business logic related to
 * estimating and calculating how long a customer has been
 * waiting in the smart queue.
 * </p>
 *
 * <p>
 * The waiting time is calculated from the queue entry's
 * joinedAt timestamp until the current time.
 * </p>
 */
public class WaitingTimeService {

    /**
     * Calculates the current waiting time of a queue entry
     * in minutes.
     *
     * @param queueEntry queue entry
     * @return waiting time in minutes
     */
    public long calculateWaitingMinutes(
            QueueEntry queueEntry
    ) {

        validateQueueEntry(queueEntry);

        LocalDateTime joinedAt =
                queueEntry.getJoinedAt();

        if (joinedAt == null) {
            return 0;
        }

        LocalDateTime now =
                LocalDateTime.now();

        if (joinedAt.isAfter(now)) {
            return 0;
        }

        return Duration.between(
                joinedAt,
                now
        ).toMinutes();
    }

    /**
     * Calculates the current waiting time in seconds.
     *
     * @param queueEntry queue entry
     * @return waiting time in seconds
     */
    public long calculateWaitingSeconds(
            QueueEntry queueEntry
    ) {

        validateQueueEntry(queueEntry);

        LocalDateTime joinedAt =
                queueEntry.getJoinedAt();

        if (joinedAt == null) {
            return 0;
        }

        LocalDateTime now =
                LocalDateTime.now();

        if (joinedAt.isAfter(now)) {
            return 0;
        }

        return Duration.between(
                joinedAt,
                now
        ).getSeconds();
    }

    /**
     * Calculates waiting time between two timestamps.
     *
     * @param joinedAt time at which customer joined queue
     * @param currentTime current/reference time
     * @return waiting time in minutes
     */
    public long calculateWaitingMinutes(
            LocalDateTime joinedAt,
            LocalDateTime currentTime
    ) {

        if (joinedAt == null) {
            throw new IllegalArgumentException(
                    "Joined time cannot be null."
            );
        }

        if (currentTime == null) {
            throw new IllegalArgumentException(
                    "Current time cannot be null."
            );
        }

        if (joinedAt.isAfter(currentTime)) {
            return 0;
        }

        return Duration.between(
                joinedAt,
                currentTime
        ).toMinutes();
    }

    /**
     * Calculates the estimated waiting time based on
     * the customer's position and average service time.
     *
     * <p>
     * Formula:
     *
     * <br>
     * Estimated Waiting Time =
     * Customers Ahead × Average Service Time
     * </p>
     *
     * @param queuePosition current queue position
     * @param averageServiceMinutes average service time
     * @return estimated waiting time in minutes
     */
    public long estimateWaitingMinutes(
            int queuePosition,
            long averageServiceMinutes
    ) {

        if (queuePosition <= 0) {
            throw new IllegalArgumentException(
                    "Queue position must be greater than zero."
            );
        }

        if (averageServiceMinutes < 0) {
            throw new IllegalArgumentException(
                    "Average service time cannot be negative."
            );
        }

        int customersAhead =
                queuePosition - 1;

        return (long) customersAhead
                * averageServiceMinutes;
    }

    /**
     * Calculates estimated waiting time using the number
     * of customers ahead.
     *
     * @param customersAhead number of customers ahead
     * @param averageServiceMinutes average service time
     * @return estimated waiting time in minutes
     */
    public long estimateWaitingTime(
            int customersAhead,
            long averageServiceMinutes
    ) {

        if (customersAhead < 0) {
            throw new IllegalArgumentException(
                    "Customers ahead cannot be negative."
            );
        }

        if (averageServiceMinutes < 0) {
            throw new IllegalArgumentException(
                    "Average service time cannot be negative."
            );
        }

        return (long) customersAhead
                * averageServiceMinutes;
    }

    /**
     * Converts minutes into a user-friendly display format.
     *
     * <p>
     * Examples:
     * <br>
     * 0 → "0 minutes"
     * <br>
     * 1 → "1 minute"
     * <br>
     * 65 → "1 hour 5 minutes"
     * </p>
     *
     * @param totalMinutes waiting time in minutes
     * @return formatted waiting time
     */
    public String formatWaitingTime(
            long totalMinutes
    ) {

        if (totalMinutes < 0) {
            throw new IllegalArgumentException(
                    "Waiting time cannot be negative."
            );
        }

        long hours =
                totalMinutes / 60;

        long minutes =
                totalMinutes % 60;

        if (hours == 0) {

            if (minutes == 1) {
                return "1 minute";
            }

            return minutes + " minutes";
        }

        if (minutes == 0) {

            if (hours == 1) {
                return "1 hour";
            }

            return hours + " hours";
        }

        String hourText =
                hours == 1
                        ? "1 hour"
                        : hours + " hours";

        String minuteText =
                minutes == 1
                        ? "1 minute"
                        : minutes + " minutes";

        return hourText + " " + minuteText;
    }

    /**
     * Calculates waiting time and directly returns
     * a formatted value.
     *
     * @param queueEntry queue entry
     * @return formatted waiting time
     */
    public String getFormattedWaitingTime(
            QueueEntry queueEntry
    ) {

        long minutes =
                calculateWaitingMinutes(
                        queueEntry
                );

        return formatWaitingTime(minutes);
    }

    /**
     * Checks whether a queue entry has exceeded the
     * specified maximum waiting time.
     *
     * @param queueEntry queue entry
     * @param maximumWaitingMinutes maximum allowed waiting time
     * @return true if maximum waiting time has been exceeded
     */
    public boolean hasExceededWaitingTime(
            QueueEntry queueEntry,
            long maximumWaitingMinutes
    ) {

        validateQueueEntry(queueEntry);

        if (maximumWaitingMinutes < 0) {
            throw new IllegalArgumentException(
                    "Maximum waiting time cannot be negative."
            );
        }

        long waitingMinutes =
                calculateWaitingMinutes(
                        queueEntry
                );

        return waitingMinutes >
                maximumWaitingMinutes;
    }

    /**
     * Calculates waiting time using the current timestamp.
     *
     * @param joinedAt queue joining timestamp
     * @return waiting time in minutes
     */
    public long calculateWaitingMinutes(
            LocalDateTime joinedAt
    ) {

        if (joinedAt == null) {
            throw new IllegalArgumentException(
                    "Joined time cannot be null."
            );
        }

        return calculateWaitingMinutes(
                joinedAt,
                LocalDateTime.now()
        );
    }

    /**
     * Validates a queue entry.
     *
     * @param queueEntry queue entry to validate
     */
    private void validateQueueEntry(
            QueueEntry queueEntry
    ) {

        Objects.requireNonNull(
                queueEntry,
                "Queue entry cannot be null."
        );
    }
}
