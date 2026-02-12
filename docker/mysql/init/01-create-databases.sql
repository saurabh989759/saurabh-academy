-- Create databases if they don't exist
CREATE DATABASE IF NOT EXISTS academy_db;
CREATE DATABASE IF NOT EXISTS academy_db_dev;

-- Grant privileges to academy_user
GRANT ALL PRIVILEGES ON academy_db.* TO 'academy_user'@'%';
GRANT ALL PRIVILEGES ON academy_db_dev.* TO 'academy_user'@'%';
FLUSH PRIVILEGES;

