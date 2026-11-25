# Referencia de Endpoints — PoE Shop Monolito API

Documentación técnica de todos los endpoints REST de la API (`monolito`), optimizada para ser interpretada por agentes de IA y clientes automatizados.

---

## 1. Contexto global

- **Base URL local:** `http://localhost:8080`
- **Base URL producción:** definida dinámicamente mediante la variable de entorno `PORT` (default `8080`).
- **Content-Type:** `application/json` (los endpoints `/api/v2/products` devuelven JSON enriquecido con hipermedios HATEOAS / HAL).
- **Autenticación y seguridad:**
  - CSRF deshabilitado.
  - Según `SecurityConfig`, todas las rutas están configuradas con `anyRequest().permitAll()`, por lo que **no se exige token JWT ni sesión** en los headers HTTP.
  - Las operaciones asociadas a un usuario reciben `{userId}` directamente en el path (obtenido previamente desde `POST /api/v1/auth/register` o `POST /api/v1/auth/login`).
- **CORS habilitado para:** `http://localhost:5173`, `http://localhost:3000`, `https://*.vercel.app`, `https://*.onrender.com`, `https://*.netlify.app` (`GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`, `allowCredentials: true`).
- **Formato de errores (`400 Bad Request`):**
  - Error de regla de negocio (`IllegalArgumentException`):
    ```json
    {
      "message": "Descripción del error"
    }
    ```
  - Error de validación de campos (`MethodArgumentNotValidException`):
    ```json
    {
      "campoConError": "Mensaje de validación del campo",
      "message": "Primer mensaje de validación encontrado"
    }
    ```

---

## 2. Enums y tipos de referencia

| Enum | Valores permitidos | Uso |
| :--- | :--- | :--- |
| `Role` | `ROLE_USER`, `ROLE_ADMIN` | Rol del usuario en `AuthResponse` |
| `OrderStatus` | `PENDING`, `COMPLETED`, `CANCELLED` | Estado de la orden en `OrderResponse` y actualización de estado |
| `MessageStatus` | `UNREAD`, `IN_REVIEW`, `RESOLVED` | Estado del ticket de soporte en `ContactResponse` y filtro/actualización |

---

## 3. Esquemas de respuesta (DTOs)

### `AuthResponse`
```json
{
  "id": 1,
  "name": "ExileName",
  "email": "exile@wraeclast.com",
  "role": "ROLE_USER"
}
```

### `ProductResponse`
```json
{
  "id": 1,
  "name": "Divine Orb",
  "price": 1.50,
  "imageUrl": "https://...",
  "altText": "Divine Orb",
  "stock": 100,
  "active": true
}
```

### `CartResponse`
```json
{
  "cartId": 1,
  "userId": 1,
  "items": [
    {
      "id": 10,
      "productId": 1,
      "productName": "Divine Orb",
      "unitPrice": 1.50,
      "quantity": 2,
      "subtotal": 3.00,
      "imageUrl": "https://..."
    }
  ],
  "totalAmount": 3.00
}
```

### `OrderResponse`
```json
{
  "id": 1,
  "orderNumber": "ORD-2026-A1B2C3",
  "userId": 1,
  "userName": "ExileName",
  "userEmail": "exile@wraeclast.com",
  "totalAmount": 3.00,
  "status": "PENDING",
  "createdAt": "2026-11-24T23:50:00",
  "items": [
    {
      "id": 5,
      "productId": 1,
      "productName": "Divine Orb",
      "priceAtPurchase": 1.50,
      "quantity": 2,
      "subtotal": 3.00,
      "imageUrl": "https://..."
    }
  ]
}
```

### `ContactResponse`
```json
{
  "id": 1,
  "name": "ExileName",
  "email": "exile@wraeclast.com",
  "subject": "Consulta sobre pedido",
  "message": "Detalle del mensaje enviado por el usuario.",
  "status": "UNREAD",
  "createdAt": "2026-11-24T23:50:00"
}
```

---

## 4. Resumen rápido de endpoints

| Dominio | Método | Ruta | Propósito | Status OK |
| :--- | :--- | :--- | :--- | :--- |
| Health | `GET` | `/api/v1/health` | Estado del servicio | `200` |
| OpenAPI | `GET` | `/v3/api-docs` | Contrato OpenAPI JSON | `200` |
| Auth | `POST` | `/api/v1/auth/register` | Registrar usuario y crear carrito | `201` |
| Auth | `POST` | `/api/v1/auth/login` | Iniciar sesión | `200` |
| Auth | `POST` | `/api/v1/auth/reset-password` | Restablecer contraseña por email | `200` |
| Users | `GET` | `/api/v1/users/{userId}` | Obtener perfil | `200` |
| Users | `PUT` | `/api/v1/users/{userId}/profile` | Actualizar nombre del perfil | `200` |
| Users | `PATCH` | `/api/v1/users/{userId}/change-password` | Cambiar contraseña | `200` |
| Products v1 | `GET` | `/api/v1/products` | Listar productos activos | `200` |
| Products v1 | `GET` | `/api/v1/products/{id}` | Obtener producto activo por ID | `200` |
| Products v1 | `POST` | `/api/v1/products` | Crear producto (Admin) | `201` |
| Products v1 | `PATCH` | `/api/v1/products/{id}/stock` | Actualizar stock (Admin) | `200` |
| Products v2 | `GET` | `/api/v2/products` | Listar productos activos (HATEOAS) | `200` |
| Products v2 | `GET` | `/api/v2/products/{id}` | Detalle de producto activo (HATEOAS) | `200` |
| Cart | `GET` | `/api/v1/cart/{userId}` | Obtener carrito del usuario | `200` |
| Cart | `POST` | `/api/v1/cart/{userId}/items` | Agregar producto al carrito | `200` |
| Cart | `PATCH` | `/api/v1/cart/{userId}/items/{itemId}` | Actualizar cantidad de un ítem | `200` |
| Cart | `DELETE` | `/api/v1/cart/{userId}/items/{itemId}` | Quitar ítem del carrito | `200` |
| Cart | `DELETE` | `/api/v1/cart/{userId}` | Vaciar carrito completo | `200` |
| Orders | `POST` | `/api/v1/orders/checkout/{userId}` | Crear orden desde el carrito | `201` |
| Orders | `GET` | `/api/v1/orders/user/{userId}` | Historial de órdenes del usuario | `200` |
| Orders | `GET` | `/api/v1/orders/{orderNumber}` | Buscar orden por código | `200` |
| Orders | `GET` | `/api/v1/orders` | Listar todas las órdenes (Admin) | `200` |
| Orders | `PATCH` | `/api/v1/orders/{orderId}/status` | Actualizar estado de orden (Admin) | `200` |
| Contact | `POST` | `/api/v1/contact` | Enviar mensaje de contacto | `201` |
| Contact | `GET` | `/api/v1/contact` | Listar mensajes (filtro opcional) | `200` |
| Contact | `GET` | `/api/v1/contact/{id}` | Ver detalle de mensaje | `200` |
| Contact | `PATCH` | `/api/v1/contact/{id}/status` | Cambiar estado de ticket (Admin) | `200` |

---

## 5. Detalle por recurso

### 5.1 Health & OpenAPI

#### `GET /api/v1/health`
- **Descripción:** Endpoint liviano de health check.
- **Respuesta `200 OK`:**
  ```json
  {
    "status": "UP",
    "service": "monolito",
    "timestamp": "2026-11-24T23:50:00Z"
  }
  ```

#### `GET /v3/api-docs`
- **Descripción:** Especificación OpenAPI 3 generada automáticamente por Springdoc (`springdoc.api-docs.path`).

---

### 5.2 Autenticación (`/api/v1/auth`)

#### `POST /api/v1/auth/register`
- **Descripción:** Registra un usuario nuevo con rol `ROLE_USER` y crea su carrito persistente vacío.
- **Request Body (`RegisterRequest`):**
  - `name` (`string`, obligatorio, `@NotBlank`, longitud `2..100`)
  - `email` (`string`, obligatorio, `@NotBlank`, `@Email`, debe ser único)
  - `password` (`string`, obligatorio, `@NotBlank`, mínimo `4` caracteres)
- **Respuesta `201 Created`:** `AuthResponse`

#### `POST /api/v1/auth/login`
- **Descripción:** Verifica correo y contraseña (BCrypt) y devuelve los datos del usuario.
- **Request Body (`LoginRequest`):**
  - `email` (`string`, obligatorio, `@NotBlank`, `@Email`)
  - `password` (`string`, obligatorio, `@NotBlank`)
- **Respuesta `200 OK`:** `AuthResponse`

#### `POST /api/v1/auth/reset-password`
- **Descripción:** Restablece la contraseña de un usuario buscando por correo.
- **Request Body (`ResetPasswordRequest`):**
  - `email` (`string`, obligatorio, `@NotBlank`, `@Email`)
  - `newPassword` (`string`, obligatorio, `@NotBlank`, mínimo `4` caracteres)
- **Respuesta `200 OK`:**
  ```json
  {
    "message": "Contraseña restablecida con éxito"
  }
  ```

---

### 5.3 Usuarios (`/api/v1/users`)

#### `GET /api/v1/users/{userId}`
- **Path Params:** `userId` (`Long`)
- **Respuesta `200 OK`:** `AuthResponse`

#### `PUT /api/v1/users/{userId}/profile`
- **Path Params:** `userId` (`Long`)
- **Request Body (`UpdateProfileRequest`):**
  - `name` (`string`, obligatorio, `@NotBlank`, longitud `2..100`)
- **Respuesta `200 OK`:** `AuthResponse`

#### `PATCH /api/v1/users/{userId}/change-password`
- **Path Params:** `userId` (`Long`)
- **Request Body (`ChangePasswordRequest`):**
  - `currentPassword` (`string`, obligatorio, `@NotBlank`)
  - `newPassword` (`string`, obligatorio, `@NotBlank`, mínimo `4` caracteres)
- **Respuesta `200 OK`:**
  ```json
  {
    "message": "Contraseña actualizada exitosamente"
  }
  ```

---

### 5.4 Productos v1 (`/api/v1/products`)

#### `GET /api/v1/products`
- **Descripción:** Devuelve únicamente los productos con `active = true`.
- **Respuesta `200 OK`:** `ProductResponse[]`

#### `GET /api/v1/products/{id}`
- **Path Params:** `id` (`Long`)
- **Descripción:** Devuelve el producto si existe y tiene `active = true`. De lo contrario devuelve `400 Bad Request`.
- **Respuesta `200 OK`:** `ProductResponse`

#### `POST /api/v1/products`
- **Descripción:** Crea un nuevo producto (Admin). El campo `name` debe ser único.
- **Request Body (`ProductRequest`):**
  - `name` (`string`, obligatorio, `@NotBlank`)
  - `price` (`number`, obligatorio, `@NotNull`, `>= 0.01`)
  - `imageUrl` (`string`, opcional)
  - `altText` (`string`, opcional)
  - `stock` (`integer`, obligatorio, `@NotNull`, `>= 0`)
  - `active` (`boolean`, opcional; si es `null` toma `true`)
- **Respuesta `201 Created`:** `ProductResponse`

#### `PATCH /api/v1/products/{id}/stock`
- **Path Params:** `id` (`Long`)
- **Query Params:** `quantity` (`Integer`, obligatorio, debe ser `>= 0`)
- **Descripción:** Fija el nuevo stock del producto (incluso si está inactivo, busca por `id`).
- **Respuesta `200 OK`:** `ProductResponse`

---

### 5.5 Productos v2 — HATEOAS (`/api/v2/products`)

#### `GET /api/v2/products`
- **Descripción:** Lista los productos activos (`active = true`) en formato `CollectionModel<EntityModel<ProductResponse>>`.
- **Enlaces HATEOAS:**
  - Colección: `self` -> `/api/v2/products`
  - Cada elemento: `self` (`/api/v2/products/{id}`), `all-products` (`/api/v2/products`), `update-stock` (`/api/v1/products/{id}/stock`).
- **Respuesta `200 OK`:** `CollectionModel<EntityModel<ProductResponse>>`

#### `GET /api/v2/products/{id}`
- **Path Params:** `id` (`Long`)
- **Descripción:** Detalle de producto activo envuelto en `EntityModel<ProductResponse>` con enlaces `self`, `all-products` y `update-stock`.
- **Respuesta `200 OK`:** `EntityModel<ProductResponse>`

---

### 5.6 Carrito (`/api/v1/cart`)

#### `GET /api/v1/cart/{userId}`
- **Path Params:** `userId` (`Long`)
- **Respuesta `200 OK`:** `CartResponse`

#### `POST /api/v1/cart/{userId}/items`
- **Path Params:** `userId` (`Long`)
- **Descripción:** Agrega un producto activo al carrito (crea el carrito si no existe). Si el producto ya está en el carrito, suma la cantidad y valida que el total no supere `product.stock`.
- **Request Body (`AddToCartRequest`):**
  - `productId` (`Long`, obligatorio, `@NotNull`)
  - `quantity` (`Integer`, obligatorio, `@NotNull`, `>= 1`)
- **Respuesta `200 OK`:** `CartResponse`

#### `PATCH /api/v1/cart/{userId}/items/{itemId}`
- **Path Params:**
  - `userId` (`Long`)
  - `itemId` (`Long` — **importante:** es el `id` del `CartItem` dentro de `CartResponse.items[].id`, **no** `productId`)
- **Query Params:** `quantity` (`Integer`, obligatorio)
- **Reglas:**
  - Si `quantity <= 0`, elimina el ítem del carrito.
  - Si `quantity > product.stock`, devuelve `400 Bad Request`.
- **Respuesta `200 OK`:** `CartResponse`

#### `DELETE /api/v1/cart/{userId}/items/{itemId}`
- **Path Params:** `userId` (`Long`), `itemId` (`Long` — `CartItem.id`)
- **Respuesta `200 OK`:** `CartResponse`

#### `DELETE /api/v1/cart/{userId}`
- **Path Params:** `userId` (`Long`)
- **Descripción:** Elimina todos los ítems del carrito del usuario.
- **Respuesta `200 OK`:** `CartResponse`

---

### 5.7 Órdenes (`/api/v1/orders`)

#### `POST /api/v1/orders/checkout/{userId}`
- **Path Params:** `userId` (`Long`)
- **Descripción:** Convierte el carrito del usuario en una orden en estado `PENDING`.
- **Reglas y efectos secundarios:**
  - Falla con `400 Bad Request` si el carrito no existe, está vacío o algún producto no tiene stock suficiente.
  - Descuenta el stock de cada producto comprado.
  - Congela el precio actual en `priceAtPurchase`.
  - Genera `orderNumber` con formato `ORD-<AÑO>-<6_CARACTERES_HEX_MAYUS>`.
  - Vacía el carrito del usuario.
- **Request Body:** Ninguno.
- **Respuesta `201 Created`:** `OrderResponse`

#### `GET /api/v1/orders/user/{userId}`
- **Path Params:** `userId` (`Long`)
- **Descripción:** Historial de órdenes del usuario ordenadas por `createdAt` descendente.
- **Respuesta `200 OK`:** `OrderResponse[]`

#### `GET /api/v1/orders/{orderNumber}`
- **Path Params:** `orderNumber` (`String`, ej. `ORD-2026-A1B2C3`)
- **Respuesta `200 OK`:** `OrderResponse`

#### `GET /api/v1/orders`
- **Descripción:** Lista todas las órdenes registradas ordenadas por `createdAt` descendente (Admin).
- **Respuesta `200 OK`:** `OrderResponse[]`

#### `PATCH /api/v1/orders/{orderId}/status`
- **Path Params:** `orderId` (`Long`)
- **Request Body (`UpdateOrderStatusRequest`):**
  - `status` (`OrderStatus`: `PENDING` | `COMPLETED` | `CANCELLED`, obligatorio, `@NotNull`)
- **Efecto secundario:** Si `status` cambia a `CANCELLED` y la orden no estaba previamente en `CANCELLED`, repone el stock de todos los ítems de la orden.
- **Respuesta `200 OK`:** `OrderResponse`

---

### 5.8 Contacto y Soporte (`/api/v1/contact`)

#### `POST /api/v1/contact`
- **Descripción:** Crea un mensaje/ticket de soporte con estado inicial `UNREAD`.
- **Request Body (`ContactRequest`):**
  - `name` (`string`, obligatorio, `@NotBlank`, longitud `2..100`)
  - `email` (`string`, obligatorio, `@NotBlank`, `@Email`)
  - `subject` (`string`, obligatorio, `@NotBlank`, longitud `3..150`)
  - `message` (`string`, obligatorio, `@NotBlank`, mínimo `10` caracteres)
- **Respuesta `201 Created`:** `ContactResponse`

#### `GET /api/v1/contact`
- **Descripción:** Lista los mensajes de soporte ordenados por `createdAt` descendente (Admin).
- **Query Params:** `status` (`MessageStatus`: `UNREAD` | `IN_REVIEW` | `RESOLVED`, opcional)
- **Respuesta `200 OK`:** `ContactResponse[]`

#### `GET /api/v1/contact/{id}`
- **Path Params:** `id` (`Long`)
- **Respuesta `200 OK`:** `ContactResponse`

#### `PATCH /api/v1/contact/{id}/status`
- **Path Params:** `id` (`Long`)
- **Request Body (`UpdateMessageStatusRequest`):**
  - `status` (`MessageStatus`: `UNREAD` | `IN_REVIEW` | `RESOLVED`, obligatorio, `@NotNull`)
- **Respuesta `200 OK`:** `ContactResponse`
