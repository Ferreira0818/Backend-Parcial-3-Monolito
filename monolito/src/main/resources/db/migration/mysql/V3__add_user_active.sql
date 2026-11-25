-- ==========================================================
-- SCRIPT DE MIGRACIÓN (V3) - SOFT DELETE DE USUARIOS (MYSQL)
-- ==========================================================
ALTER TABLE users
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
