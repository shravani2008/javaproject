package config;

/**
 * Central application configuration for the Smart Queue Management System.
 *
 * <p>This class keeps application-wide constants in one place instead of
 * scattering configuration values throughout the project.</p>
 *
 * <p>Database credentials can be supplied through environment variables:</p>
 *
 * <ul>
 *     <li>SMART_QUEUE_DB_URL</li>
 *     <li>SMART_QUEUE_DB_USER</li>
 *     <li>SMART_QUEUE_DB_PASSWORD</li>
 * </ul>
 *
 * <p>Default values are provided for local development.</p>
 */
public final class AppConfig {

    private AppConfig() {
        // Prevent object creation.
    }

    // ============================================================
    // APPLICATION INFORMATION
    // ============================================================

    public static final String APPLICATION_NAME =
            "Smart Queue Management System";

    public static final String APPLICATION_VERSION =
            "1.0.0";

    public static final String APPLICATION_DESCRIPTION =
            "A professional Java-based queue management platform "
            + "for appointments, tokens, staff operations and analytics.";

    // ============================================================
    // DATABASE CONFIGURATION
    // ============================================================

    private static final String DEFAULT_DATABASE_URL =
            "jdbc:mysql://localhost:3306/smart_queue_db"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";

    private static final String DEFAULT_DATABASE_USER =
            "root";

    private static final String DEFAULT_DATABASE_PASSWORD =
            "";

    /**
     * JDBC database URL.
     *
     * <p>The environment variable allows the configuration to be changed
     * without modifying source code.</p>
     */
    public static final String DATABASE_URL =
            getEnvironmentValue(
                    "SMART_QUEUE_DB_URL",
                    DEFAULT_DATABASE_URL
            );

    /**
     * Database username.
     */
    public static final String DATABASE_USER =
            getEnvironmentValue(
                    "SMART_QUEUE_DB_USER",
                    DEFAULT_DATABASE_USER
            );

    /**
     * Database password.
     */
    public static final String DATABASE_PASSWORD =
            getEnvironmentValue(
                    "SMART_QUEUE_DB_PASSWORD",
                    DEFAULT_DATABASE_PASSWORD
            );

    // ============================================================
    // QUEUE CONFIGURATION
    // ============================================================

    /**
     * Maximum number of active queue entries supported by the application.
     */
    public static final int MAX_ACTIVE_QUEUE_ENTRIES = 500;

    /**
     * Default estimated service time in minutes.
     */
    public static final int DEFAULT_SERVICE_TIME_MINUTES = 10;

    /**
     * Minimum allowed service time.
     */
    public static final int MIN_SERVICE_TIME_MINUTES = 1;

    /**
     * Maximum allowed service time.
     */
    public static final int MAX_SERVICE_TIME_MINUTES = 120;

    /**
     * Number of tokens that can be displayed in the customer queue view.
     */
    public static final int DEFAULT_QUEUE_DISPLAY_LIMIT = 20;

    // ============================================================
    // APPOINTMENT CONFIGURATION
    // ============================================================

    /**
     * Default appointment duration in minutes.
     */
    public static final int DEFAULT_APPOINTMENT_DURATION_MINUTES = 15;

    /**
     * Minimum advance time required before booking an appointment.
     */
    public static final int MIN_APPOINTMENT_ADVANCE_MINUTES = 30;

    /**
     * Maximum number of days for advance appointment booking.
     */
    public static final int MAX_APPOINTMENT_ADVANCE_DAYS = 90;

    // ============================================================
    // SECURITY CONFIGURATION
    // ============================================================

    /**
     * Minimum password length accepted by the application.
     */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Maximum password length accepted by the application.
     */
    public static final int MAX_PASSWORD_LENGTH = 72;

    /**
     * Number of BCrypt hashing rounds.
     *
     * <p>This value is used by PasswordUtil when BCrypt is configured.</p>
     */
    public static final int PASSWORD_HASH_ROUNDS = 12;

    // ============================================================
    // UI CONFIGURATION
    // ============================================================

    public static final double APPLICATION_WIDTH = 1280;

    public static final double APPLICATION_HEIGHT = 800;

    public static final double MIN_APPLICATION_WIDTH = 1000;

    public static final double MIN_APPLICATION_HEIGHT = 650;

    public static final String DEFAULT_FONT_FAMILY =
            "System";

    // ============================================================
    // DATE AND TIME CONFIGURATION
    // ============================================================

    public static final String DATE_FORMAT =
            "dd-MM-yyyy";

    public static final String TIME_FORMAT =
            "HH:mm";

    public static final String DATE_TIME_FORMAT =
            "dd-MM-yyyy HH:mm";

    // ============================================================
    // QUEUE STATUS VALUES
    // ============================================================

    public static final String STATUS_WAITING =
            "WAITING";

    public static final String STATUS_SERVING =
            "SERVING";

    public static final String STATUS_COMPLETED =
            "COMPLETED";

    public static final String STATUS_CANCELLED =
            "CANCELLED";

    public static final String STATUS_SKIPPED =
            "SKIPPED";

    // ============================================================
    // USER ROLE VALUES
    // ============================================================

    public static final String ROLE_ADMIN =
            "ADMIN";

    public static final String ROLE_STAFF =
            "STAFF";

    public static final String ROLE_CUSTOMER =
            "CUSTOMER";

    // ============================================================
    // APPOINTMENT STATUS VALUES
    // ============================================================

    public static final String APPOINTMENT_SCHEDULED =
            "SCHEDULED";

    public static final String APPOINTMENT_CONFIRMED =
            "CONFIRMED";

    public static final String APPOINTMENT_COMPLETED =
            "COMPLETED";

    public static final String APPOINTMENT_CANCELLED =
            "CANCELLED";

    public static final String APPOINTMENT_NO_SHOW =
            "NO_SHOW";

    // ============================================================
    // PRIVATE HELPER
    // ============================================================

    /**
     * Reads a configuration value from an environment variable.
     *
     * @param variableName environment variable name
     * @param defaultValue value used when the variable is unavailable
     * @return configured value
     */
    private static String getEnvironmentValue(
            String variableName,
            String defaultValue) {

        String value = System.getenv(variableName);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value.trim();
    }
}