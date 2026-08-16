CREATE DATABASE IF NOT EXISTS scm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'scm_app'@'localhost' IDENTIFIED BY 'change_me';
GRANT ALL PRIVILEGES ON scm_db.* TO 'scm_app'@'localhost';
FLUSH PRIVILEGES;