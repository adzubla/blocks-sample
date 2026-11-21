-- No unique constraint on order_id: Phase 1 records a duplicate charge instead of preventing
-- it. TODO(idempotency).
CREATE TABLE payments (
    id                     UUID PRIMARY KEY,
    order_id               UUID           NOT NULL,
    amount                 NUMERIC(12, 2) NOT NULL,
    status                 VARCHAR(20)    NOT NULL,
    gateway_transaction_id VARCHAR(100),
    failure_reason         VARCHAR(500),
    created_at             TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX payments_order_id_idx ON payments (order_id);
