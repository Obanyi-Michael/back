-- Database Migration Script
-- Add missing columns to users table

-- Add bio column
ALTER TABLE users ADD COLUMN IF NOT EXISTS bio VARCHAR(500);

-- Add status column  
ALTER TABLE users ADD COLUMN IF NOT EXISTS status VARCHAR(50);

-- Add is_online column
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_online BOOLEAN DEFAULT FALSE;

-- Add last_seen column
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_seen TIMESTAMP;

-- Update existing records to have default values
UPDATE users SET is_online = FALSE WHERE is_online IS NULL;
UPDATE users SET last_seen = NOW() WHERE last_seen IS NULL; 