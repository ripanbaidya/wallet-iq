INSERT INTO users (id, created_at, updated_at, full_name, email, password_hash, role, active, is_email_verified)
VALUES (gen_random_uuid(),
        now(),
        now(),
        'admin1',
        'admin1@walletiq.com',
        '$2a$10$oJqjKPyJUjkwAV1CWFR2f.Nv8WC8KFvkaODUebBguzGa7Yubd.oB2',
        'ADMIN',
        true,
        true)
ON CONFLICT (email) DO NOTHING;