CREATE DATABASE IF NOT EXISTS keycloak;

create table categories (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    description TEXT,
    is_active boolean
);

create table users (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username varchar(100) UNIQUE,
    keycloak_id varchar(255) UNIQUE,
    email varchar(100) UNIQUE,
    role ENUM('ADMIN', 'USER'),
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

create table products (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(100),
    description TEXT,
    price decimal(10, 2),
    stock_quantity integer,
    category_id integer,
    CONSTRAINT FK_PRODUCT_CATEGORY FOREIGN KEY (category_id) REFERENCES categories(id),
    created_at timestamp,
    version INT DEFAULT 0,
    is_active boolean
);

create table orders (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id integer,
    total_amount decimal(10, 2),
    status ENUM(
        'PENDING',
        'CONFIRMED',
        'SHIPPED',
        'DELIVERED',
        'CANCELLED'
    ),
    CONSTRAINT FK_ORDER_USER FOREIGN KEY (user_id) REFERENCES users(id),
    created_at timestamp
);

create table order_items (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id integer,
    product_id integer,
    quantity integer,
    unit_price decimal(10, 2),
    CONSTRAINT FK_OITEM_ORDER FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT FK_OITEM_PRODUCT FOREIGN KEY (product_id) REFERENCES products(id)
);