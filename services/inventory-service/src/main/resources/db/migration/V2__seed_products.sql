-- Demo catalog. The payment gateway declines charges above 1000, so:
--   KEYBOARD/MOUSE orders succeed, a LAPTOP order is declined, MONITOR runs out quickly.
INSERT INTO products (sku, name, price, available_quantity) VALUES
    ('KEYBOARD', 'Mechanical keyboard', 49.90,  100),
    ('MOUSE',    'Wireless mouse',      19.90,  100),
    ('MONITOR',  '27" monitor',         299.00,   3),
    ('LAPTOP',   '14" laptop',          1499.00, 10);
