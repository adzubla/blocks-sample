CREATE TABLE orders (
    id                  UUID PRIMARY KEY,
    customer_email      VARCHAR(255) NOT NULL,
    status              VARCHAR(20)  NOT NULL,
    cancellation_reason VARCHAR(500),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE order_lines (
    order_id   UUID          NOT NULL REFERENCES orders (id),
    line_no    INT           NOT NULL,
    sku        VARCHAR(50)   NOT NULL,
    quantity   INT           NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    PRIMARY KEY (order_id, line_no)
);
