-- One Postgres instance, one database and one user per service. A service can only reach its
-- own database: no shared tables between bounded contexts.
CREATE USER order_svc WITH PASSWORD 'order_svc';
CREATE DATABASE orders OWNER order_svc;

CREATE USER inventory_svc WITH PASSWORD 'inventory_svc';
CREATE DATABASE inventory OWNER inventory_svc;

CREATE USER payment_svc WITH PASSWORD 'payment_svc';
CREATE DATABASE payments OWNER payment_svc;
