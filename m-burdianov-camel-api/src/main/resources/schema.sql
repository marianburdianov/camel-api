CREATE DATABASE IF NOT EXISTS shop_management_db;

USE shop_management_db;

CREATE TABLE IF NOT EXISTS employees
(
    employee_id BIGINT NOT NULL AUTO_INCREMENT,
    first_name  VARCHAR(30) NULL,
    last_name   VARCHAR(30) NULL,
    gender      VARCHAR(10) NULL,
    birth_date  DATE NULL,
    hire_date   DATE NULL,
    role        VARCHAR(20) NULL,
    salary      DOUBLE NULL,
    PRIMARY KEY (employee_id)
);

CREATE TABLE IF NOT EXISTS customers
(
    customer_id  BIGINT       NOT NULL AUTO_INCREMENT,
    first_name   VARCHAR(30)  NULL,
    last_name    VARCHAR(30)  NULL,
    phone_number BIGINT       NULL,
    address      VARCHAR(256) NULL,
    PRIMARY KEY (customer_id)
);

CREATE TABLE IF NOT EXISTS products
(
    product_id BIGINT      NOT NULL AUTO_INCREMENT,
    name       VARCHAR(30) NOT NULL,
    price      DOUBLE      NULL,
    quantity   INTEGER     NULL,
    PRIMARY KEY (product_id)
);

CREATE TABLE IF NOT EXISTS purchaseinfos
(
    purchaseinfo_id BIGINT  NOT NULL AUTO_INCREMENT,
    employee_id     BIGINT  NOT NULL,
    customer_id     BIGINT  NOT NULL,
    cost            DOUBLE  NULL,
    amount          INTEGER NULL,
    date            DATE    NULL,
    CONSTRAINT purchase_employee_id
        FOREIGN KEY (employee_id)
            REFERENCES shop_management_db.employees (employee_id)
            ON DELETE CASCADE,
    CONSTRAINT purchase_customer_id
        FOREIGN KEY (customer_id)
            REFERENCES shop_management_db.customers (customer_id)
            ON DELETE CASCADE,
    PRIMARY KEY (purchaseinfo_id)
);

CREATE TABLE IF NOT EXISTS purchaseinfos_products
(
    purchaseinfo_id BIGINT,
    product_id      BIGINT,
    unique (purchaseinfo_id, product_id),
    CONSTRAINT fk_purchase_id
        FOREIGN KEY (purchaseinfo_id)
            REFERENCES shop_management_db.purchaseinfos (purchaseinfo_id)
            ON DELETE SET NULL
            ON UPDATE SET NULL,
    CONSTRAINT fk_product_id
        FOREIGN KEY (product_id)
            REFERENCES shop_management_db.products (product_id)
            ON DELETE SET NULL
            ON UPDATE SET NULL
);