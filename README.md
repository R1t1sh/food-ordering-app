
# Online Food Ordering System

A full-stack Java-based platform enabling restaurants to manage online orders, track order status, process payments, and handle customer feedback. The platform includes modules for menu management, order handling, and integrated payment services.

## 🔧 Modules Created

This project consists of the following key modules:

- **Authentication Module**
  - User Registration & Login
  - Session management
  - Security with Role-based access(user,manager,admin)

- **Customer Module**
  - Add, update, delete, view customers
  - Link to cart and order history

- **Restaurant Module**
  - Register restaurants
  - Managed by Admin
- **Food Cart Module**
  - Add or remove items to/from cart
  - View cart details

- **Item Module**
  - Manage individual food items under a category
  - CRUD operations by restaurant managers

- **Category Module**
  - Group items under food categories
  - Linked to restaurants
- **Menu Management Module**: Allows restaurants to manage categories and items in their menus.
- **Order Management Module**: Supports adding items to cart, placing orders, and tracking them.
- **Billing Module**
  - Generate bills with total amount and item breakdown
  - Linked to customer and order
- **Payment Module**: Includes bill generation, payment processing, and status tracking.
  - Razorpay integration
  - Bill generation and payment tracking
- **Feedback Module**: Enables customers to submit feedback and ratings.
- **Logging and Error Handling**: Logs system errors and transaction details using SLF4J / Logback.
- **Testing**: Ensures robustness through unit tests written using JUnit.

## 🧠 Tasks Performed

- Designed and implemented relational database schema using Hibernate (JPA) and MySQL 

- Developed RESTful APIs using Spring Boot

- Implemented authentication and session management

- Integrated cart and checkout with order tracking

- Integrated Razorpay for real-time payment processing

- Added feedback and restaurant rating system 

- Logged transactions and handled exceptions gracefully
- Validated core modules using JUnit testing

## 🚀 How to Run the Project

### Prerequisites

- Java 17 or higher
- Spring Boot
- Maven
- MySQL
- IDE (e.g., IntelliJ, Eclipse, or VS Code)
- Postman

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/restaurant-order-system.git
   ```

2. Navigate into the project directory:
   ```bash
   cd Food_Delivery_App
   ```

3. Configure the application properties in:
   `src/main/resources/application.properties`

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/foodapp
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```
   ```properties
   razorpay.key_id=your_razorpay_key_id
   razorpay.key_secret=your_razorpay_key_secret

  
   ```
4. Assign `ADMIN`, `MANAGER`, `EMPLOYEE(USER)`roles in database as all roles are users(employees) bydefault. 


5. Run the project:
   - From IDE: Run `FoodDeliveryAppApplication.java`
   - From terminal:
     ```bash
     mvn spring-boot:run
     ```

6. Access API endpoints via Postman at:Few sample API endpoints to test flow of app 
   ```
   POST-http://localhost:8008/auth/signUp
   POST=http://localhost:8008/auth/login
   POST-http://localhost:8008/customer/add?key=usersessionid
   POST-http://localhost:8008/restaurant/add?key=adminsessionid
   POST-http://localhost:8008/category/add?key=adminsessionid
   POST-http://localhost:8008/item/add?key=adminsessionid
   POST-http://localhost:8008/cart/register?key=usersessionid
   POST-http://localhost:8008/order/checkout/{cartId}?key=usersessionid
   GET-http://localhost:8008/bill/view/billId
   POST-http://localhost:8008/payment/initiate/billId
   POST-http://localhost:8008/feedback/submit

   
   ```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── foodapp/
│   │           └────
│   │               ├── controller/
│   │               │   ├── BillServiceController.java
│   │               │   ├── CategoryServiceController.java
│   │               │   ├── CustomerServiceController.java
│   │               │   ├── FeedbackController.java
│   │               │   ├── FoodCartServiceController.java
│   │               │   ├── ItemServiceController.java
│   │               │   ├── OrderDetailServiceController.java
│   │               │   ├── PaymentController.java
│   │               │   └── RestaurantServiceController.java
│   │               │
│   │               ├── service/
│   │               │   ├── BillService.java
│   │               │   ├── CategoryService.java
│   │               │   ├── CustomerService.java
│   │               │   ├── FeedbackService.java
│   │               │   ├── FoodCartService.java
│   │               │   ├── ItemService.java
│   │               │   ├── OrderDetailService.java
│   │               │   ├── PaymentService.java
│   │               │   └── RestaurantService.java
│   │               │
│   │               ├── repository/
│   │               │   ├── BillDAO.java
│   │               │   ├── CategoryDAO.java
│   │               │   ├── CustomerDAO.java
│   │               │   ├── FeedbackDAO.java
│   │               │   ├── FoodCartDAO.java
│   │               │   ├── ItemDAO.java
│   │               │   ├── OrderDAO.java
│   │               │   ├── PaymentRepository.java
│   │               │   └── RestaurantDAO.java
│   │               │
│   │               ├── model/
│   │               │   ├── Address.java
│   │               │   ├── Bill.java
│   │               │   ├── Category.java
│   │               │   ├── Customer.java
│   │               │   ├── Feedback.java
│   │               │   ├── FoodCart.java
│   │               │   ├── Item.java
│   │               │   ├── OrderDetails.java
│   │               │   ├── Payment.java
│   │               │   └── Restaurant.java
│   │               │
│   │               ├── exception/
│   │               │   ├── BillException.java
│   │               │   ├── CartException.java
│   │               │   ├── CategoryException.java
│   │               │   ├── CustomerException.java
│   │               │   ├── ErrorDetails.java
│   │               │   ├── GlobalExceptionHandler.java
│   │               │   ├── ItemException.java
│   │               │   ├── OrderException.java
│   │               │   ├── PaymentException.java
│   │               │   └── RestaurantException.java
│   │               │
│   │               └── FoodDeliveryAppApplication.java
│   └── resources/
│       ├── application.properties
│       |
│       ├── static/
│       │   └── style.css
│       └── templates/
│           ├── login.html
│           └── signup.html
│       
│
└── test/
    └── java/
        └──  com/
            └── foodapp/
                ├── controller/
                │   └── (Controller test classes)
                ├── service/
                │   └── (Service test classes)
                └── FoodDeliveryAppApplication.java

```

## 🗃️ Database Structure
  
### Tables & Relationships

---

### 🧾 Address Table

| Column     | Type    | Constraints                 |
|------------|---------|-----------------------------|
| addressId  | INT     | PRIMARY KEY, AUTO_INCREMENT |
| area       | VARCHAR |                             |
| city       | VARCHAR |                             |
| state      | VARCHAR |                             |
| country    | VARCHAR |                             |
| pincode    | VARCHAR |                             |

---

### 🧑‍💼 Customer Table

| Column       | Type    | Constraints                       |
|--------------|---------|------------------------------------|
| customerId   | INT     | PRIMARY KEY, AUTO_INCREMENT       |
| fullName     | VARCHAR | NOT NULL                          |
| age          | INT     |                                    |
| gender       | VARCHAR |                                    |
| mobileNumber | VARCHAR | UNIQUE, NOT NULL                  |
| email        | VARCHAR | UNIQUE, NOT NULL                  |
| addressId    | INT     | FOREIGN KEY → Address(addressId)  |
| cartId       | INT     | FOREIGN KEY → FoodCart(cartId)    |

---

### 🍽️ Restaurant Table

| Column         | Type    | Constraints                      |
|----------------|---------|----------------------------------|
| restaurantId   | INT     | PRIMARY KEY, AUTO_INCREMENT     |
| restaurantName | VARCHAR | NOT NULL                        |
| managerName    | VARCHAR |                                  |
| contactNumber  | VARCHAR | NOT NULL                        |
| addressId      | INT     | FOREIGN KEY → Address(addressId) |

---

### 📂 Category Table

| Column       | Type    | Constraints                         |
|--------------|---------|--------------------------------------|
| categoryId   | INT     | PRIMARY KEY, AUTO_INCREMENT         |
| categoryName | VARCHAR | NOT NULL                            |
| restaurantId | INT     | FOREIGN KEY → Restaurant            |

---

### 🍔 Item Table

| Column     | Type    | Constraints                          |
|------------|---------|---------------------------------------|
| itemId     | INT     | PRIMARY KEY, AUTO_INCREMENT          |
| itemName   | VARCHAR | NOT NULL                             |
| quantity   | INT     |                                       |
| cost       | DOUBLE  |                                       |
| categoryId | INT     | FOREIGN KEY → Category(categoryId)   |
| cartId     | INT     | FOREIGN KEY → FoodCart(cartId)       |
| orderId    | INT     | FOREIGN KEY → OrderDetails(orderId)  |

---

### 🛒 FoodCart Table

| Column    | Type | Constraints                     |
|-----------|------|----------------------------------|
| cartId    | INT  | PRIMARY KEY, AUTO_INCREMENT     |
| customerId| INT  | FOREIGN KEY → Customer          |

---

### 📦 OrderDetails Table

| Column      | Type     | Constraints                        |
|-------------|----------|-------------------------------------|
| orderId     | INT      | PRIMARY KEY, AUTO_INCREMENT        |
| orderDate   | DATETIME | NOT NULL                           |
| orderStatus | VARCHAR  | DEFAULT 'PENDING'                  |
| customerId  | INT      | FOREIGN KEY → Customer(customerId) |

---

### 🧾 Bill Table

| Column     | Type     | Constraints                          |
|------------|----------|---------------------------------------|
| billId     | INT      | PRIMARY KEY, AUTO_INCREMENT          |
| billDate   | DATETIME | NOT NULL                             |
| totalCost  | DOUBLE   |                                       |
| totalItem  | INT      |                                       |
| status     | VARCHAR  | DEFAULT 'UNPAID'                     |
| orderId    | INT      | FOREIGN KEY → OrderDetails(orderId)  |

---

### 💳 Payment Table

| Column       | Type    | Constraints                  |
|--------------|---------|-------------------------------|
| paymentId    | INT     | PRIMARY KEY, AUTO_INCREMENT  |
| paymentStatus| VARCHAR | DEFAULT 'PENDING'            |
| paymentMode  | VARCHAR |                              |
| billId       | INT     | FOREIGN KEY → Bill(billId)   |

---

### 💬 Feedback Table

| Column     | Type    | Constraints                            |
|------------|---------|-----------------------------------------|
| feedbackId | INT     | PRIMARY KEY, AUTO_INCREMENT            |
| comment    | TEXT    |                                         |
| rating     | INT     |                                         |
| customerId | INT     | FOREIGN KEY → Customer(customerId)     |
| itemId     | INT     | FOREIGN KEY → Item(itemId)             |
| orderId    | INT     | FOREIGN KEY → OrderDetails(orderId)    |

---
### Entities and Relationships

- **Address** (`addressId` PK)  
  Referenced by: `Customer`, `Restaurant` (OneToOne)

- **Customer** (`customerId` PK)  
  OneToOne with `Address`, `FoodCart`  
  OneToMany with `Feedback`, `OrderDetails`

- **Restaurant** (`restaurantId` PK)  
  OneToOne with `Address`  
  OneToMany with `Item`, `Category`

- **Category** (`categoryId` PK)  
  ManyToOne with `Restaurant`  
  OneToOne with `Item`

- **Item** (`itemId` PK)  
  ManyToOne with `OrderDetails`, `FoodCart`  
  OneToOne with `Category`  
  OneToMany with `Feedback`

- **FoodCart** (`cartId` PK)  
  OneToOne with `Customer`  
  OneToMany with `Item`

- **OrderDetails** (`orderId` PK)  
  ManyToOne with `Customer`  
  OneToMany with `Item`  
  OneToOne with `Bill`, `Feedback`

- **Bill** (`billId` PK)  
  OneToOne with `OrderDetails`, `Payment`

- **Payment** (`paymentId` PK)  
  OneToOne with `Bill`

- **Feedback** (`feedbackId` PK)  
  ManyToOne with `Customer`, `Item`  
  OneToOne with `OrderDetails`


## 🔐 Authentication & Authorization

- Session-based login and signup
- Roles defined: `ADMIN`, `MANAGER`, `EMPLOYEE(USER)`
- Secured endpoints with auto-generated Session ID

## 🔍 Testing

- Written using **JUnit**
- Coverage includes:
  - Controller testing for REST endpoints
  - Service-level business logic
- Postman collection for API testing

## 🚀 Future Enhancements

- Admin panel for restaurant management
- JWT-based authentication and authorization
- Menu image upload and file storage
- Order status notifications (email/SMS)
