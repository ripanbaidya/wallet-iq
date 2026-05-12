-- Admin registration is intentionally restricted.
-- No public API endpoints are exposed for creating admin accounts.
-- Admin users must be created directly at the system/database level.

-- To create a new admin account:
-- 1. Generate an encoded password using Spring Security's PasswordEncoder.
-- 2. Use the helper/test file provided in the test directory to generate the hash.
-- 3. Insert the admin record manually into the database.

-- A default admin account is already provisioned:
-- Email    : admin1@walletiq.com
-- Password : password12345

INSERT INTO users (id, created_at, updated_at, full_name, email, password_hash, role, active, is_email_verified)
VALUES (
           gen_random_uuid(),
           now(),
           now(),
           'admin1',
           'admin1@walletiq.com',
           '$2a$10$y8RxtjTTBHEp2GemllrBb.BJBVH.LEj6hDoSJ4J1KzqovZzMbXkGa',
           'ADMIN',
           true,
           true
       )
ON CONFLICT (email) DO NOTHING;