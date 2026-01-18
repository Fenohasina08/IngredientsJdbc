CREATE TYPE unit_type AS ENUM ('PCS',u 'KG', 'L');
CREATE TABLE DishIngredient (
                                id SERIAL PRIMARY KEY,
                                id_dish INT REFERENCES dish(id),
                                id_ingredient INT REFERENCES ingredient(id),
                                quantity_required NUMERIC(10, 2),
                                unit unit_type
);