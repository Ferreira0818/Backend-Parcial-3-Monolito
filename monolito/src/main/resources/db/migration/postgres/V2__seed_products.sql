-- ==========================================================
-- SCRIPT DE MIGRACIÓN (V2) - SEEDER DE PRODUCTOS (POSTGRESQL)
-- ==========================================================

INSERT INTO products (name, price, image_url, alt_text, stock, active) VALUES
('Divine Orb', 2.10, '/images/divine.webp', 'Divine Orb - Moneda principal de trade en endgame', 450, TRUE),
('Chaos Orb', 0.05, '/images/chaos.webp', 'Chaos Orb - Moneda estándar para ítems de bajo y medio valor', 12000, TRUE),
('Mirror of Kalandra', 185.00, '/images/mirror.webp', 'Mirror of Kalandra - El orbe más raro y valioso de Wraeclast', 8, TRUE),
('Exalted Orb', 0.15, '/images/exalted.webp', 'Exalted Orb - Clásico orbe para agregar modificadores', 3500, TRUE),
('Orb of Annulment', 0.25, '/images/annul.webp', 'Orb of Annulment - Elimina un mod aleatorio para crafteo', 800, TRUE),
('Awakened Sextant', 0.40, '/images/sextant.webp', 'Awakened Sextant - Modificadores de mapas avanzados', 1500, TRUE);