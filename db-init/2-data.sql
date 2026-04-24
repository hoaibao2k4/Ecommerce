-- categories
INSERT INTO categories (name, description, is_active) VALUES
('Electronics', 'Devices, gadgets, and electronic accessories', true),
('Fashion', 'Clothing, shoes, and fashion accessories', true),
('Books', 'Printed books, novels, and study materials', true),
('Home & Kitchen', 'Household and kitchen essentials', true),
('Sports', 'Sportswear and fitness equipment', true);

-- users
INSERT INTO users (username, email, role) VALUES
('admin', 'admin@example.com', 'ADMIN'),
('john_doe', 'johndoe@example.com', 'USER'),
('alice_nguyen', 'alice@example.com', 'USER'),
('mike_tran', 'mike@example.com', 'USER'),
('sophia_le', 'sophia@example.com', 'USER');

-- products
INSERT INTO products (name, description, price, stock_quantity, category_id, created_at, is_active) VALUES
('iPhone 15', 'Latest Apple smartphone', 999.99, 20, 1, CURRENT_TIMESTAMP, true),
('Sony WH-1000XM5', 'Noise-canceling wireless headphones', 349.99, 100, 1, CURRENT_TIMESTAMP, true),
('Logitech MX Master 3S', 'Ergonomic wireless mouse', 99.00, 150, 1, CURRENT_TIMESTAMP, true),
('MacBook Air M3', '13-inch, 16GB RAM, 512GB SSD', 1099.00, 20, 1, CURRENT_TIMESTAMP, true),
('Samsung S24 Ultra', '512GB, Titanium Grey, S-Pen included', 1199.00, 30, 1, CURRENT_TIMESTAMP, true),

('Nike Air Jordan 1', 'High-top basketball sneakers, OG colorway', 170.00, 45, 2, CURRENT_TIMESTAMP, true),
('Adidas Ultraboost', 'Comfortable running shoes with boost sole', 180.00, 60, 2, CURRENT_TIMESTAMP, true),
('Levis 501 Original', 'Classic straight-leg blue denim jeans', 69.50, 200, 2, CURRENT_TIMESTAMP, true),
('Uniqlo Oversized Tee', 'Minimalist cotton oversized fit tee', 19.90, 500, 2, CURRENT_TIMESTAMP, true),
('Zara Leather Jacket', 'Premium faux leather jacket for autumn', 89.00, 25, 2, CURRENT_TIMESTAMP, true),

('Clean Code', 'A handbook of agile software craftsmanship', 45.00, 50, 3, CURRENT_TIMESTAMP, true),
('The Pragmatic Programmer', 'David Thomas anniversary edition', 38.50, 180, 3, CURRENT_TIMESTAMP, true),
('Atomic Habits', 'James Clear - Build Good Habits', 18.00, 1000, 3, CURRENT_TIMESTAMP, true),
('Eloquent JavaScript', 'Marijn Haverbeke - Modern Intro to JS', 29.90, 220, 3, CURRENT_TIMESTAMP, true),
('Harry Potter Box Set', 'Complete series collection by J.K. Rowling', 75.00, 40, 3, CURRENT_TIMESTAMP, true),

('Blender', 'Multi-purpose kitchen blender', 75.99, 15, 4, CURRENT_TIMESTAMP, true),
('IKEA Floor Lamp', 'Nordic style brass finish lamp', 45.00, 80, 4, CURRENT_TIMESTAMP, true),
('Modern Velvet Sofa', '3-seater navy blue velvet luxury sofa', 750.00, 10, 4, CURRENT_TIMESTAMP, true),
('Ceramic Coffee Table', 'Minimalist white ceramic top table', 120.00, 15, 4, CURRENT_TIMESTAMP, true),
('Decorative Wall Clock', 'Silent quartz movement wooden clock', 35.00, 120, 4, CURRENT_TIMESTAMP, true),

('Yoga Mat', 'Non-slip exercise yoga mat', 25.99, 40, 5, CURRENT_TIMESTAMP, true),
('Dyson Airwrap Styler', 'Complete hair styling set blue-copper', 599.00, 8, 5, CURRENT_TIMESTAMP, true),
('Chanel No. 5 Perfume', 'Classic luxury fragrance for women', 145.00, 50, 5, CURRENT_TIMESTAMP, true),
('Laneige Lip Mask', 'Berry sleeping mask for smooth lips', 22.00, 400, 5, CURRENT_TIMESTAMP, true),
('La Roche-Posay SPF50', 'Anthelios UVmune 400 invisible fluid', 19.00, 600, 5, CURRENT_TIMESTAMP, true),
('Clinique Face Cream', 'Moisture Surge 100H auto-replenishing', 42.00, 150, 5, CURRENT_TIMESTAMP, true);

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