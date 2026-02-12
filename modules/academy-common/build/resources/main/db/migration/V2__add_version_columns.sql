-- Add version columns for optimistic locking
ALTER TABLE students ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE batches ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE mentor_sessions ADD COLUMN version BIGINT DEFAULT 0;

-- Update existing records to have version 0
UPDATE students SET version = 0 WHERE version IS NULL;
UPDATE batches SET version = 0 WHERE version IS NULL;
UPDATE mentor_sessions SET version = 0 WHERE version IS NULL;

