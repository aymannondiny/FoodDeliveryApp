# 🍔 Food Delivery Application

A complete Java food delivery platform with a Swing desktop UI and REST API,
built with clean OOP principles and JSON file-based persistence.

---

## 📁 Project Structure

```
FoodDeliveryApp/
├── pom.xml                          ← Maven build (Java 17+, Gson 2.10)
├── data/                            ← Auto-created JSON data files at runtime
│   ├── users.json
│   ├── restaurants.json
│   ├── menu_items.json
│   ├── orders.json
│   ├── riders.json
│   ├── coupons.json
│   └── payments.json
└── src/main/java/com/fooddelivery/
    ├── Main.java                    ← Entry point: starts API + Swing window
    │
    ├── model/                       ← Domain layer (pure POJOs)
    │   ├── Address.java             ← Physical address with Haversine distance
    │   ├── Coupon.java              ← Discount coupon with validity rules
    │   ├── MenuItem.java            ← Menu item with stock & add-ons
    │   ├── MenuItemAddon.java       ← Optional add-on / customisation
    │   ├── Order.java               ← Full order lifecycle with status history
    │   ├── OrderItem.java           ← Line item inside an order
    │   ├── OrderStatus.java         ← Enum: PLACED → CONFIRMED → ... → DELIVERED
    │   ├── Payment.java             ← Payment record (COD / bKash / Nagad / Card)
    │   ├── Restaurant.java          ← Restaurant profile with schedule & rating
    │   ├── Rider.java               ← Delivery rider with assignment tracking
    │   ├── Schedule.java            ← Opening hours (days + HH:mm times)
    │   └── User.java                ← User with Role enum (CUSTOMER / OWNER / RIDER)
    │
    ├── repository/                  ← Persistence layer
    │   ├── FileRepository.java      ← Generic JSON file-backed key-value store
    │   ├── Repositories.java        ← 7 concrete singletons (User, Restaurant, …)
    │   └── RepositoryFactory.java   ← Central access point for all repos
    │
    ├── service/                     ← Business logic (one class per concern)
    │   ├── AuthService.java         ← Register, login, session management
    │   ├── CartService.java         ← In-memory shopping cart
    │   ├── CouponService.java       ← Coupon validation & discount calculation
    │   ├── MenuService.java         ← Menu CRUD, add-ons, availability
    │   ├── OrderService.java        ← Order placement, status progression
    │   ├── PaymentService.java      ← Payment processing (gateway simulation)
    │   ├── RestaurantService.java   ← Discovery, search, rating
    │   └── RiderService.java        ← Rider registration & availability
    │
    ├── api/                         ← REST HTTP endpoints (built-in HttpServer)
    │   ├── ApiServer.java           ← Starts server on port 8080
    │   ├── ApiResponse.java         ← JSON response builder & query parser
    │   └── ApiHandlers.java         ← 6 handler classes (one per endpoint)
    │
    ├── ui/                          ← Swing UI
    │   ├── UITheme.java             ← Design system (colours, fonts, factories)
    │   ├── LoginRegisterPanel.java  ← Login + register tabs
    │   ├── customer/
    │   │   ├── CustomerDashboard.java      ← Tabbed container for customers
    │   │   ├── RestaurantListPanel.java    ← Browse & search restaurants
    │   │   ├── MenuPanel.java              ← Restaurant menu + add-to-cart
    │   │   ├── CartPanel.java              ← Cart, coupon, checkout
    │   │   └── OrderHistoryPanel.java      ← Orders + live tracking dialog
    │   └── restaurant/
    │       ├── RestaurantDashboard.java    ← Owner: menu mgmt, live orders, settings
    │       └── RiderDashboard.java         ← Rider: pickup queue, delivery progress
    │
    └── util/
        ├── AppUtils.java            ← ID generator, SHA-256 hasher, validators
        ├── DataSeeder.java          ← Seeds 3 restaurants, menus, users, coupons
        ├── LocalDateTimeAdapter.java← Gson ↔ LocalDateTime serialiser
        └── LocalDateAdapter.java    ← Gson ↔ LocalDate serialiser
```

---

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher (JDK)
- Maven 3.6+

### Build & Run

```bash
# 1. Clone / extract the project
cd FoodDeliveryApp

# 2. Build fat JAR (includes Gson)
mvn clean package -q

# 3. Run
java -jar target/food-delivery-app-jar-with-dependencies.jar
```

Or run directly without building:

```bash
mvn exec:java -Dexec.mainClass=com.fooddelivery.Main
```

---

## 👤 Demo Accounts (seeded automatically)

| Role             | Email                   | Password    |
|------------------|-------------------------|-------------|
| Customer         | rahim@example.com       | password123 |
| Restaurant Owner | kamal@example.com       | password123 |
| Restaurant Owner | nasrin@example.com      | password123 |
| Rider            | farhan@example.com      | password123 |

### Demo Coupons
| Code       | Discount | Min Order | Cap      |
|------------|----------|-----------|----------|
| WELCOME20  | 20%      | 150 BDT   | 100 BDT  |
| FLAT50     | 50%      | 300 BDT   | 50 BDT   |
| NEWUSER    | 15%      | 100 BDT   | 80 BDT   |

---

## 🌐 REST API Endpoints (port 8080)

The API server starts automatically alongside the Swing UI.

### 1. Get Restaurants
```
GET http://localhost:8080/api/restaurants
GET http://localhost:8080/api/restaurants?area=Dhanmondi
GET http://localhost:8080/api/restaurants?cuisine=Chinese
GET http://localhost:8080/api/restaurants?search=burger
```

**Sample response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "RST-XXXXXXXX",
      "name": "Dhaka Bites",
      "cuisineType": "Bangladeshi",
      "rating": 4.5,
      "isOpen": true,
      "minOrderAmount": 100.0,
      "estimatedDeliveryMin": 30,
      "address": { "area": "Dhanmondi", "city": "Dhaka", "lat": 23.7512, "lng": 90.3752 }
    }
  ]
}
```

### 2. Get Menu
```
GET http://localhost:8080/api/menu?restaurantId=RST-XXXXXXXX
```

### 3. Get Order Details
```
GET http://localhost:8080/api/order?orderId=ORD-XXXXXXXX
```

### 4. Track Order (lightweight)
```
GET http://localhost:8080/api/track?orderId=ORD-XXXXXXXX
```

### 5. Get All Cuisine Types
```
GET http://localhost:8080/api/cuisines
```

### 6. Validate Coupon
```
GET http://localhost:8080/api/coupon?code=WELCOME20&subtotal=500
```

---

## 🍽 User Flows

### Customer Flow
1. **Login** → Customer Dashboard
2. **Restaurants tab** → Browse / search / filter by cuisine
3. **View Menu →** → Add items to cart (with add-ons & special instructions)
4. **Cart tab** → Review items, apply coupon, set delivery address + payment method
5. **Place Order** → Auto-routed to Orders tab
6. **My Orders tab** → Track status, simulate status progression (demo button), cancel if early

### Restaurant Owner Flow
1. **Login** → Restaurant Dashboard (or register a new restaurant)
2. **Live Orders tab** → See incoming orders, advance status (Confirmed → Preparing → Ready)
3. **Menu tab** → Add / delete items, toggle availability, manage add-ons, update stock
4. **Settings tab** → Update name, phone, description, min order, schedule

### Rider Flow
1. **Login** → Rider Dashboard
2. **Toggle availability**
3. **Current Assignment tab** → Accept pickup of READY orders, mark On the Way → Delivered
4. **All Deliveries tab** → History of completed deliveries
5. **Stats tab** → Total deliveries + earnings

---

## 🗄 Data Model

```
User ──────────────┐
                   │ owns
                   ▼
Restaurant ────────┬──── Schedule (open hours + days)
         │         │
         │ has many▼
         │        MenuItem ──── MenuItemAddon (0..*)
         │
         │ receives
         ▼
Order ──────────────┬──── OrderItem (1..*)  ← references MenuItem
      │             │
      │ assigned to │ paid by
      ▼             ▼
    Rider         Payment
      │
      │ applied
      ▼
    Coupon
```

---

## ⚙ Design Decisions

| Concern              | Approach                                                        |
|----------------------|-----------------------------------------------------------------|
| **Persistence**      | JSON files via Gson; `FileRepository<T>` generic base class     |
| **Singleton repos**  | One instance per entity type, accessed via `RepositoryFactory`  |
| **Password storage** | SHA-256 hex hash (`AppUtils.hashPassword`)                      |
| **Distance**         | Haversine formula in `Address.distanceTo()`                     |
| **Cart**             | In-memory singleton; cleared on logout / order placement        |
| **API**              | Java built-in `com.sun.net.httpserver.HttpServer` (no framework)|
| **Payment**          | Simulated gateway (95 % success); COD marked on delivery        |
| **UI**               | Java Swing with custom `UITheme` design system                  |
| **Concurrency**      | `ConcurrentHashMap` in `FileRepository` for thread safety       |

---

## 📦 Dependencies

| Library | Version | Purpose               |
|---------|---------|-----------------------|
| Gson    | 2.10.1  | JSON serialisation    |
| JUnit 5 | 5.10.0  | Unit tests (optional) |

No external web framework or database required.

---

## 🔧 Extending the Project

- **Add database**: Swap `FileRepository` with a JDBC implementation behind the same interface
- **Real payments**: Replace `PaymentService.simulateGateway()` with bKash / Nagad API calls
- **Push notifications**: Add a `NotificationService` that email/SMS on status changes
- **Admin panel**: Create `AdminDashboard` (role already exists in `User.Role.ADMIN`)
- **Web frontend**: Call the REST API from any framework; CORS headers are already set
