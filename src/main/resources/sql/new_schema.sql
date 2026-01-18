CREATE TYPE unit_type AS ENUM ('PCS','KG', 'L');
CREATE TABLE DishIngredient (
                                id SERIAL PRIMARY KEY,
                                id_dish INT REFERENCES dish(id),
                                id_ingredient INT REFERENCES ingredient(id),
                                quantity_required NUMERIC(10, 2),
                                unit unit_type
);
ALTER TABLE dish ADD COLUMN selling_price NUMERIC(10, 2);
ALTER TABLE ingredient DROP COLUMN id_dish;

INSERT INTO DishIngredient (id_dish, id_ingredient, quantity_required, unit) VALUES
                                                                                 (1, 1, 0.20, 'KG'),
                                                                                 (1, 2, 0.15, 'KG'),
                                                                                 (2, 3, 1.00, 'KG'),
                                                                                 (4, 4, 0.30, 'KG'),
                                                                                 (4, 5, 0.20, 'KG');

UPDATE dish SET selling_price = 3500.00 WHERE id = 1;
UPDATE dish SET selling_price = 12000.00 WHERE id = 2;
UPDATE dish SET selling_price = 8000.00 WHERE id = 4;