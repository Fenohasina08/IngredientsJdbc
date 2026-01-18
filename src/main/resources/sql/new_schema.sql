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