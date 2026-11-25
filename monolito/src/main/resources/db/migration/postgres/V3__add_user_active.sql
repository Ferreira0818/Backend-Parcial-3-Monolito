-- ==========================================================
-- SCRIPT DE MIGRACIÓN (V3) - SOFT DELETE DE USUARIOS (POSTGRESQL)
-- ==========================================================
ALTER TABLE users
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
