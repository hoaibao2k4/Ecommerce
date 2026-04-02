-- categories
INSERT INTO categories (name, description, is_active) VALUES
('Electronics', 'Devices, gadgets, and electronic accessories', true),
('Fashion', 'Clothing, shoes, and fashion accessories', true),
('Books', 'Printed books, novels, and study materials', true),
('Home & Kitchen', 'Household and kitchen essentials', true),
('Sports', 'Sportswear and fitness equipment', true);

-- users
INSERT INTO users (username, email, password, role) VALUES
-- admin123
('admin', 'admin@example.com', '$2a$10$.o216JZL9QlohlU3t5lJd.JqpTV8kIO13PSf2GIb.0UzvqJWlLwRK', 'ADMIN'),
-- john123
('john_doe', 'john@example.com', '$2a$10$vhhdxIcBoXcRF1heKnStRu9jh0XL1jw3l09Yb9m8xP5tzOvsl7uVm', 'USER'),
-- alice123
('alice_nguyen', 'alice@example.com', '$2a$10$9y/MR3/wOq4HgyfiemneK.dnb0SCFAA4BDj1X8F8HciXA0Gn.quDi', 'USER'),
-- mike123
('mike_tran', 'mike@example.com', '$2a$10$2CBFb8CHawozEUlsCAple.DlRe7zl7mP1dZUS4D/5SiEGAhDNrE0y', 'USER'),
-- sophia123
('sophia_le', 'sophia@example.com', '$2a$10$dViYDT1OXqQVFDzKmC0EeuvwA2v0n84S7LAEzi7TwJBex1UN3vgGO', 'USER');

-- products
INSERT INTO products (name, description, price, stock_quantity, category_id, created_at, is_active) VALUES
('iPhone 15', 'Latest Apple smartphone', 999.99, 20, 1, CURRENT_TIMESTAMP, true),
('Nike Air Max', 'Comfortable running shoes', 120.50, 35, 2, CURRENT_TIMESTAMP, true),
('Clean Code', 'A handbook of agile software craftsmanship', 45.00, 50, 3, CURRENT_TIMESTAMP, true),
('Blender', 'Multi-purpose kitchen blender', 75.99, 15, 4, CURRENT_TIMESTAMP, true),
('Yoga Mat', 'Non-slip exercise yoga mat', 25.99, 40, 5, CURRENT_TIMESTAMP, true);

-- orders
INSERT INTO orders (user_id, total_amount, status, created_at) VALUES
(2, 999.99, 'PENDING', CURRENT_TIMESTAMP),
(3, 120.50, 'CONFIRMED', CURRENT_TIMESTAMP),
(4, 45.00, 'SHIPPED', CURRENT_TIMESTAMP),
(5, 75.99, 'DELIVERED', CURRENT_TIMESTAMP),
(2, 51.98, 'CANCELLED', CURRENT_TIMESTAMP);

-- order_items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1, 1, 1, 999.99),
(2, 2, 1, 120.50),
(3, 3, 1, 45.00),
(4, 4, 1, 75.99),
(5, 5, 2, 25.99);