# Spring Boot E-commerce API

A robust, secure, and high-performance e-commerce backend built with Spring Boot, JPA, and MySQL. 

## Features
- **Security**: JWT Authentication & RBAC (Admin/User).
- **Audit**: Automatic tracking of creation time using JPA Auditing.
- **Race Condition Protection**: Optimistic Locking with `@Version` for inventory integrity.
- **Financial Precision**: Using `BigDecimal` for all monetary calculations.
- **Robust Filtering**: Advanced product & order filtering using JPQL and Specifications.
- **Unified Logic**: Flat and intuitive API response structure.

---

## Setup & Installation

### Prerequisites
- **Java 17** or higher.
- **Maven** 3.8+.
- **MySQL** 8.0+.
- **Docker** (Optional, for database).

### 1. Database Configuration
Create a database named `ecommerce` in your MySQL instance. The project is configured to automatically initialize tables and seed data from `db-init/` when it first runs.

### 2. Environment Variables
Create a `.env` file in the root directory (compatible with `${env}` injection) or set them in your system properties:

| Variable | Description | Default |
| :--- | :--- | :--- |
| `DB_HOST` | MySQL database host | `localhost` |
| `DB_PORT` | MySQL database port | `3306` |
| `DB_NAME` | Database name | `ecommerce` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `password` |
| `JWT_SECRET` | Secret key for signing tokens | (Required) |
| `JWT_ACCESS_EXPIRATION` | Access token lifespan (ms) | (Required) |
| `JWT_REFRESH_EXPIRATION` | Refresh token lifespan (ms) | (Required) |
| `SERVER_PORT` | Port for the application | `8080` |

### 3. Running the Project
```bash
# Clone the repository
git clone <repo-url>
cd ecommerce

# Compile and package
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080/api/v1`

---

## Authentication & Accounts

The system uses JWT for security. Use the `/auth/login` endpoint to obtain tokens.

### Sample Accounts (Default Seed)
| Role | Username | Password |
| :--- | :--- | :--- |
| **ADMIN** | `admin` | `admin123` |
| **USER** | `john_doe` | `john123` |
| **USER** | `alice_nguyen` | `alice123` |

---

## API Endpoints

### Auth
- `POST /auth/register` - Create new user account.
- `POST /auth/login` - Get access & refresh tokens.
- `POST /auth/refresh-token` - Renew access token.

### Products
- `GET /products/all` - List all active products.
- `GET /products` - Filter products (page/size/keyword/categoryId/price).
- `GET /products/{id}` - Get product details.
- `POST /products` - [ADMIN] Add new product.
- `PUT /products/{id}` - [ADMIN] Update product.
- `DELETE /products/{id}` - [ADMIN] Soft delete product.

### Orders
- `POST /orders` - [USER] Place a new order.
- `GET /orders/my-orders` - [USER] View your order history.
- `GET /orders` - [ADMIN] Manage & search all orders.
- `PATCH /orders/{id}/status` - [ADMIN] Update order status.

---

## Design Decisions
- **Consistency**: All error responses follow a standardized `ErrorResponse` schema.
- **Soft Deletes**: Categories and Products use `isActive` flag to maintain order history integrity.
- **Validation**: Strict type checking and logic sequence (e.g., Status Transition rules).
