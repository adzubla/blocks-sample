CREATE TABLE products (
    sku                VARCHAR(50) PRIMARY KEY,
    name               VARCHAR(255)   NOT NULL,
    price              NUMERIC(12, 2) NOT NULL,
    available_quantity INT            NOT NULL CHECK (available_quantity >= 0)
);

CREATE TABLE reservations (
    order_id   UUID PRIMARY KEY,
    status     VARCHAR(20)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE reservation_lines (
    order_id UUID        NOT NULL REFERENCES reservations (order_id),
    sku      VARCHAR(50) NOT NULL,
    quantity INT         NOT NULL
);
