-- 1. Nettoyage (Ordre important pour les contraintes de clés étrangères)
DROP TABLE IF EXISTS DishIngredient CASCADE;
DROP TABLE IF EXISTS ingredient CASCADE;
DROP TABLE IF EXISTS dish CASCADE;
DROP TYPE IF EXISTS unit_type CASCADE;
DROP TYPE IF EXISTS dish_type CASCADE;
DROP TYPE IF EXISTS ingredient_category CASCADE;

-- 2. Création des types ENUM
CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L'); -- [cite: 36]
CREATE TYPE dish_type AS ENUM ('START', 'MAIN', 'DESSERT', 'STARTER'); -- [cite: 7, 14]
CREATE TYPE ingredient_category AS ENUM ('MEAT', 'VEGETABLE', 'DAIRY', 'FRUIT', 'SPICE');

-- 3. Table Dish (Plat)
CREATE TABLE dish (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      dish_type dish_type NOT NULL,
                      selling_price NUMERIC(10, 2) -- Ajout du prix de vente [cite: 50]
);

-- 4. Table Ingredient (Normalisée : plus de id_dish ici)
CREATE TABLE ingredient (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            price NUMERIC(10, 2) NOT NULL, -- Coût unitaire [cite: 40]
                            category ingredient_category NOT NULL
);

-- 5. Table de jointure DishIngredient
CREATE TABLE DishIngredient (
                                id SERIAL PRIMARY KEY, -- [cite: 31]
                                id_dish INT REFERENCES dish(id) ON DELETE CASCADE, -- [cite: 32]
                                id_ingredient INT REFERENCES ingredient(id) ON DELETE CASCADE, -- [cite: 33]
                                quantity_required NUMERIC(10, 2) NOT NULL, -- [cite: 34]
                                unit unit_type NOT NULL -- [cite: 35]
);

-- 6. Insertion des plats (Données du TD)
INSERT INTO dish (id, name, dish_type, selling_price) VALUES
                                                          (1, 'Salade fraîche', 'START', 3500.00), -- [cite: 55]
                                                          (2, 'Poulet grillé', 'MAIN', 12000.00), -- [cite: 55]
                                                          (3, 'Riz aux légumes', 'MAIN', NULL),     -- [cite: 55]
                                                          (4, 'Gâteau au chocolat', 'DESSERT', 8000.00), -- [cite: 55]
                                                          (5, 'Salade de fruits', 'DESSERT', NULL); -- [cite: 55]

-- 7. Insertion des ingrédients (Données du TD)
INSERT INTO ingredient (id, name, price, category) VALUES
                                                       (1, 'Laitue', 800.00, 'VEGETABLE'), -- [cite: 8]
                                                       (2, 'Tomate', 600.00, 'VEGETABLE'), -- [cite: 8]
                                                       (3, 'Poulet', 4500.00, 'MEAT'),     -- [cite: 48, 64]
                                                       (4, 'Chocolat', 4000.00, 'SPICE'),   -- Valeur indicative pour test
                                                       (5, 'Sucre', 1000.00, 'SPICE');     -- Valeur indicative pour test

-- 8. Insertion dans DishIngredient (Données du TD)
INSERT INTO DishIngredient (id, id_dish, id_ingredient, quantity_required, unit) VALUES
                                                                                     (1, 1, 1, 0.20, 'KG'), -- [cite: 53]
                                                                                     (2, 1, 2, 0.15, 'KG'), -- [cite: 53]
                                                                                     (3, 2, 3, 1.00, 'KG'), -- [cite: 53]
                                                                                     (4, 4, 4, 0.30, 'KG'), -- [cite: 53]
                                                                                     (5, 4, 5, 0.20, 'KG'); -- [cite: 53]