# Webstore REST API

Spring Boot REST API for a webstore order management system.  
Built with **Java 17**, **Spring Boot 3.2**, **Spring Data JPA**, and **MariaDB 11.5**.

## Setup & Running

### Prerequisites
- Java 17+
- Maven 3.8+
- MariaDB 11.5

### Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/webstore
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Start the application


#### Run WebstoreApplication.java


The API listens on `http://localhost:8080`.

---

### Customers

#### `GET /api/customers`
List all customers. Optional `search` param filters by first and last name.

**Query params:** `search` (optional)

> **Index used: `idx_customer_lastname`** — the JPQL query performs a `LIKE` match on `last_name` and `first_name`. MariaDB uses the index on `last_name` to narrow the scan before evaluating the `first_name` condition, avoiding a full table scan on large customer datasets.

**Response:** `200 OK` — array of:
```json
{
  "id": integer,
  "firstName": string,
  "lastName": string,
  "email": string,
  "phone": string | null
}
```

---

#### `GET /api/customers/{id}`
Get a single customer with their addresses.

**Response:** `200 OK`
```json
{
  "id": integer,
  "firstName": string,
  "lastName": string,
  "email": string,
  "phone": string | null,
  "addresses": [
    {
      "id": integer,
      "streetAddress": string,
      "postalCode": string | null,
      "city": string,
      "country": string | null
    }
  ]
}
```

---

#### `POST /api/customers`
Create a new customer.

> **Index used: `idx_customer_email` (unique)** — before inserting, the service checks for a duplicate email with `existsByEmail()`. This lookup hits the unique index on `customers.email` directly instead of scanning the whole table.

**Request body:**
```json
{
  "firstName": string,
  "lastName": string,
  "email": string,
  "phone": string | null
}
```

**Response:** `201 Created`
```json
{
  "id": integer,
  "firstName": string,
  "lastName": string,
  "email": string,
  "phone": string | null
}
```

---

#### `PUT /api/customers/{id}`
Update a customer.

> **Index used: `idx_customer_email` (unique)** — same duplicate email check as on create, using the index for an O(log n) lookup.

**Request body:** same as `POST /api/customers`

**Response:** `200 OK` — same as POST response

---

#### `DELETE /api/customers/{id}`
Delete a customer (cascades to addresses).

**Response:** `204 No Content`

---

#### `GET /api/customers/{id}/addresses`
List all addresses for a customer.

> **Index used: `fk_customeraddress_customer`** — the query filters `customeraddresses` by `customer_id`, which is a foreign key index. MariaDB uses it to retrieve only the rows belonging to this customer without scanning the full addresses table.

**Response:** `200 OK` — array of:
```json
{
  "id": integer,
  "streetAddress": string,
  "postalCode": string | null,
  "city": string,
  "country": string | null
}
```

---

#### `POST /api/customers/{id}/addresses`
Add an address to a customer.

**Request body:**
```json
{
  "streetAddress": string,
  "postalCode": string | null,
  "city": string,
  "country": string | null
}
```

**Response:** `201 Created`
```json
{
  "id": integer,
  "streetAddress": string,
  "postalCode": string | null,
  "city": string,
  "country": string | null
}
```

---

#### `PUT /api/customers/{id}/addresses/{addressId}`
Update a specific address.

**Request body:** same as `POST /api/customers/{id}/addresses`

**Response:** `200 OK` — same as POST address response

---

#### `DELETE /api/customers/{id}/addresses/{addressId}`
Delete an address.

**Response:** `204 No Content`

---

### Suppliers

#### `GET /api/suppliers`
List all suppliers. Optional `search` param filters by name.

**Query params:** `search` (optional)

**Response:** `200 OK` — array of:
```json
{
  "id": integer,
  "name": string,
  "contactName": string | null,
  "phone": string | null,
  "email": string | null
}
```

---

#### `GET /api/suppliers/{id}`
Get a supplier with their addresses.

> **Index used: `fk_supplieraddress_supplier`** — addresses are fetched by `supplier_id` foreign key index, same pattern as customer addresses.

**Response:** `200 OK`
```json
{
  "id": integer,
  "name": string,
  "contactName": string | null,
  "phone": string | null,
  "email": string | null,
  "addresses": [
    {
      "id": integer,
      "streetAddress": string,
      "postalCode": string | null,
      "city": string,
      "country": string | null
    }
  ]
}
```

---

#### `POST /api/suppliers`
Create a supplier.

**Request body:**
```json
{
  "name": string,
  "contactName": string | null,
  "phone": string | null,
  "email": string | null
}
```

**Response:** `201 Created`
```json
{
  "id": integer,
  "name": string,
  "contactName": string | null,
  "phone": string | null,
  "email": string | null
}
```

---

#### `PUT /api/suppliers/{id}`
Update a supplier.

**Request body:** same as `POST /api/suppliers`

**Response:** `200 OK` — same as POST response

---

#### `DELETE /api/suppliers/{id}`
Delete a supplier.

**Response:** `204 No Content`

---

#### `GET /api/suppliers/{id}/addresses`
List supplier addresses.

> **Index used: `fk_supplieraddress_supplier`** — filters by `supplier_id` using the foreign key index.

**Response:** `200 OK` — array of:
```json
{
  "id": integer,
  "streetAddress": string,
  "postalCode": string | null,
  "city": string,
  "country": string | null
}
```

---

#### `POST /api/suppliers/{id}/addresses`
Add an address to a supplier.

**Request body:** same as `POST /api/customers/{id}/addresses`

**Response:** `201 Created` — same structure as customer address response

---

#### `DELETE /api/suppliers/{id}/addresses/{addressId}`
Delete a supplier address.

**Response:** `204 No Content`

---

### Products & Categories

#### `GET /api/products`
List products. Supports multiple optional filters (only one applied at a time):

| Param | Type | Description |
|-------|------|-------------|
| `search` | string | Search by product name |
| `categoryId` | integer | Filter by category |
| `supplierId` | integer | Filter by supplier |
| `minPrice` | decimal | Lower price bound |
| `maxPrice` | decimal | Upper price bound |

> **Index used: `idx_product_name`** — when `search` is provided, the `LIKE` query on `products.name` benefits from the index, especially for prefix-style searches.  
> **Index used: `fk_product_category`** — when filtering by `categoryId`, the foreign key index on `category_id` is used for a direct index scan instead of a full table scan.  
> **Index used: `fk_product_supplier`** — same as above for `supplierId` filtering.  
> **Index used: `idx_product_price`** — the `BETWEEN` query for price range filtering uses the index on `price` to efficiently locate the matching range without scanning all rows.

**Response:** `200 OK` — array of:
```json
{
  "id": integer,
  "name": string,
  "description": string | null,
  "price": decimal,
  "stockQuantity": integer,
  "categoryName": string | null,
  "supplierName": string | null,
  "availability": "IN_STOCK" | "LOW_STOCK" | "OUT_OF_STOCK"
}
```

`availability` is `LOW_STOCK` when `stockQuantity < 10`, `OUT_OF_STOCK` when `stockQuantity = 0`.

---

#### `GET /api/products/{id}`
Get a single product.

**Response:** `200 OK` — same as product list item

---

#### `GET /api/products/low-stock?threshold=10`
Products with stock below the threshold (default 10).

> **Index used: `idx_product_stock`** — the `WHERE stock_quantity < :threshold` condition uses the index on `stock_quantity` to scan only the relevant portion of the index rather than the entire products table.

**Response:** `200 OK` — array of product objects

---

#### `GET /api/products/out-of-stock`
Products with zero stock.

> **Index used: `idx_product_stock`** — equality lookup on `stock_quantity = 0` uses the same index as the low-stock query.

**Response:** `200 OK` — array of product objects

---

#### `POST /api/products`
Create a product.

**Request body:**
```json
{
  "name": string,
  "description": string | null,
  "price": decimal,
  "stockQuantity": integer,
  "categoryId": integer | null,
  "supplierId": integer | null
}
```

**Response:** `201 Created` — product object

---

#### `PUT /api/products/{id}`
Update a product.

**Request body:** same as `POST /api/products`

**Response:** `200 OK` — product object

---

#### `PATCH /api/products/{id}/stock`
Update only the stock quantity.

**Request body:**
```json
{ "stockQuantity": integer }
```

**Response:** `200 OK` — product object

---

#### `DELETE /api/products/{id}`
Delete a product.

**Response:** `204 No Content`

---

#### `GET /api/products/categories`
List all product categories.

**Response:** `200 OK` — array of:
```json
{
  "id": integer,
  "name": string,
  "description": string | null
}
```

---

#### `POST /api/products/categories`
Create a category.

**Request body:**
```json
{
  "name": string,
  "description": string | null
}
```

**Response:** `201 Created` — category object

---

#### `PUT /api/products/categories/{id}`
Update a category.

**Request body:** same as `POST /api/products/categories`

**Response:** `200 OK` — category object

---

#### `DELETE /api/products/categories/{id}`
Delete a category.

**Response:** `204 No Content`

---

### Orders

#### `GET /api/orders`
List orders. Supports filters (only one applied at a time):

| Param | Type | Description |
|-------|------|-------------|
| `status` | string | Filter by status: `NEW`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED` |
| `customerId` | integer | Filter by customer |
| `from` | ISO datetime | Start of date range |
| `to` | ISO datetime | End of date range |

> **Index used: `idx_order_status`** — filtering by `status` hits the index directly, which is especially effective given the low cardinality of the status column. MariaDB can resolve the full result set from the index alone.  
> **Index used: `fk_order_customer`** — filtering by `customerId` uses the foreign key index on `customer_id`, returning only that customer's orders without touching unrelated rows.  
> **Index used: `idx_order_date`** — the `BETWEEN` date range query uses the index on `order_date` to efficiently scan the relevant time window.

**Response:** `200 OK` — array of:
```json
{
  "id": integer,
  "customerId": integer,
  "customerName": string,
  "orderDate": datetime,
  "deliveryDate": datetime | null,
  "status": string,
  "totalAmount": decimal,
  "items": [
    {
      "productId": integer,
      "productName": string,
      "quantity": integer,
      "unitPrice": decimal,
      "subtotal": decimal
    }
  ]
}
```

---

#### `GET /api/orders/{id}`
Get a single order with full item details.

> Uses a JPQL `JOIN FETCH` query that loads the order, customer, items, and products in a single SQL statement, avoiding the N+1 problem that would otherwise occur when iterating over lazy-loaded collections.

**Response:** `200 OK` — order object (same structure as list item)

---

#### `POST /api/orders`
Place a new order. Runs in a single transaction — stock is checked and decremented atomically via DB triggers.

**Request body:**
```json
{
  "customerId": integer,
  "shippingAddressId": integer | null,
  "items": [
    {
      "productId": integer,
      "quantity": integer
    }
  ]
}
```

**Response:** `201 Created` — order object

**Errors:**
- `404` if customer, address, or product not found
- `409 Conflict` if stock is insufficient (raised by DB trigger)
- `400` if address doesn't belong to the customer

---

#### `PATCH /api/orders/{id}/status`
Update order status. Enforces valid state transitions:

```
NEW → PROCESSING → SHIPPED → DELIVERED
 ↓         ↓
CANCELLED CANCELLED
```

**Request body:**
```json
{ "status": string }
```

**Response:** `200 OK` — order object

Setting status to `DELIVERED` automatically records `deliveryDate`. The DB trigger `trg_order_status_change` records every transition in `order_status_history`.

---

#### `POST /api/orders/{id}/cancel`
Cancel an order. Cannot cancel a `DELIVERED` order.

**Response:** `204 No Content`

---

#### `GET /api/orders/{id}/history`
Get the full status change history for an order (populated by DB triggers).

> **Index used: `idx_status_history_order`** — the history table can grow large over time. The index on `order_id` means this query retrieves only the rows for the given order directly, without scanning the entire history table.

**Response:** `200 OK` — array of:
```json
{
  "id": integer,
  "orderId": integer,
  "oldStatus": string | null,
  "newStatus": string,
  "changedAt": datetime
}
```

---

#### `GET /api/orders/customer-stats`
Aggregated order statistics per customer.

> **Index used: `fk_order_customer`** — for each customer the service calls `findByCustomerId()`, which filters `orders` by `customer_id` using the foreign key index. Without this index the query would require a full scan of the orders table per customer.

**Response:** `200 OK` — array of:
```json
{
  "customerId": integer,
  "customerName": string,
  "email": string,
  "totalOrders": long,
  "totalSpent": decimal,
  "lastOrderDate": datetime | null
}
```

