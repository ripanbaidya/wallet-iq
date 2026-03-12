insert into admin (id, name, email, password_hash, role, created_at, updated_at)
values (gen_random_uuid(), 'Admin One', 'admin1@walletiq.com',
        '$2a$10$Jw/JaofmEUQuQVbGD.MnROWnR0C1/f1d4CGXZPd5/4CbJ3bF2cru6', 'ADMIN', now(), now()),
       (gen_random_uuid(), 'Admin Two', 'admin2@walletiq.com',
        '$2a$10$Mh77yDbyCCOswrYmTBEcw.Ik4NX0n3rcUV0jnRrDXSy81cidIe8ia', 'ADMIN', now(), now());

-- admin1 : original - "admin12345", hash - "$2a$10$Jw/JaofmEUQuQVbGD.MnROWnR0C1/f1d4CGXZPd5/4CbJ3bF2cru6"
-- admin2 : original - "admin123456", hash - "$2a$10$Mh77yDbyCCOswrYmTBEcw.Ik4NX0n3rcUV0jnRrDXSy81cidIe8ia"