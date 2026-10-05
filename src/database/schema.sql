USE smart_queue_db;

SHOW TABLES;
DESCRIBE users;
DESCRIBE customers;
DESCRIBE staff;
DESCRIBE services;
DESCRIBE counters;
DESCRIBE tokens;
DESCRIBE queue_entries;
DESCRIBE queue_history;
DESCRIBE appointments;

SELECT * FROM users;

USE smart_queue_db;

SHOW TABLES;
DESCRIBE users;
DESCRIBE customers;
DESCRIBE staff;
DESCRIBE services;
DESCRIBE counters;
DESCRIBE tokens;
DESCRIBE queue_entries;
DESCRIBE queue_history;
DESCRIBE appointments;

USE smart_queue_db;

SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_KEY,
    COLUMN_DEFAULT,
    EXTRA
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'smart_queue_db'
ORDER BY TABLE_NAME, ORDINAL_POSITION;

USE smart_queue_db;

SELECT 
    TABLE_NAME,
    GROUP_CONCAT(
        CONCAT(
            COLUMN_NAME,
            ' [', COLUMN_TYPE, ']',
            IF(IS_NULLABLE = 'NO', ' NOT NULL', ' NULL'),
            IF(COLUMN_KEY <> '', CONCAT(' ', COLUMN_KEY), '')
        )
        ORDER BY ORDINAL_POSITION
        SEPARATOR ' | '
    ) AS COLUMNS
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'smart_queue_db'
GROUP BY TABLE_NAME
ORDER BY TABLE_NAME;

SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_KEY,
    COLUMN_DEFAULT,
    EXTRA
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'smart_queue_db'
ORDER BY TABLE_NAME, ORDINAL_POSITION;

USE smart_queue_db;

SELECT user_id, username, full_name, role, active
FROM users;
USE smart_queue_db;

SELECT user_id, username, full_name, role, active
FROM users;
USE smart_queue_db;

SELECT * FROM users;

DESCRIBE users;

USE smart_queue_db;

INSERT INTO users
(username, password_hash, full_name, email, phone, role, is_active)
VALUES
('admin', 'admin123', 'System Administrator', 'admin@smartqueue.com', '9999999999', 'ADMIN', 1),
('staff', 'staff123', 'Queue Staff', 'staff@smartqueue.com', '9999999998', 'STAFF', 1),
('customer', 'customer123', 'Test Customer', 'customer@smartqueue.com', '9999999997', 'CUSTOMER', 1);
USE smart_queue_db;

SHOW TABLES;
DESCRIBE users;
DESCRIBE tokens;
DESCRIBE queue_entries;
USE smart_queue_db;

SELECT
    u.user_id,
    u.username,
    u.full_name,
    u.role,
    c.customer_id
FROM users u
LEFT JOIN customers c
    ON u.user_id = c.user_id
WHERE u.role = 'CUSTOMER';

USE smart_queue_db;

DESCRIBE customers;

USE smart_queue_db;

INSERT INTO customers (user_id, customer_type)
VALUES (3, 'NORMAL');
SELECT * FROM customers;

USE smart_queue_db;

SELECT user_id, username, full_name, role, is_active
FROM users;

SELECT user_id, username, password_hash, role
FROM users;
