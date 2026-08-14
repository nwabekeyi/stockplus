# StockPulse - Inventory Management System

A mobile-first inventory management system for stores with subscription-based access, built using the same stack as the escrow-app.

## Tech Stack

### Backend
- Java 17 + Spring Boot 4
- Spring Security + JWT (HTTP-only cookies)
- Spring Data JPA (Hibernate 6)
- PostgreSQL 14.1
- Redis
- Paystack Payment Integration
- Lombok

### Frontend
- React 19 + TypeScript
- Vite 8
- Tailwind CSS v4
- Zustand (state management)
- React Router v7
- Mobile-first responsive design

## Features

- **User Authentication**: Register/Login with JWT tokens
- **Store Management**: Each user gets their own store
- **Product Management**: Add, edit, delete products with categories
- **Stock Tracking**: Monitor inventory levels with low-stock alerts
- **Sales Recording**: Record sales with items and profit tracking
- **Subscription Management**: Admin creates plans, users subscribe via Paystack
- **Admin Panel**: Manage subscription plans and view users
- **Dashboard**: Real-time stats (products, low stock, revenue, sales)

## Getting Started

### Prerequisites
- Node.js 20+
- Java 17+
- PostgreSQL 14+
- Redis (optional, for caching)

### Backend Setup

```bash
cd api
./mvnw spring-boot:run
```

Backend runs on `http://localhost:8080`

### Frontend Setup

```bash
cd app
pnpm install
pnpm dev
```

Frontend runs on `http://localhost:5173`

### Docker

```bash
cd payguard-docker
docker-compose up --build
```

### Environment Variables

Create `.env` in the `api` directory:

```env
DB_URL=jdbc:postgresql://localhost:5432/stockmgmt
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=your-secret-key
PAYSTACK_SECRET_KEY=your-paystack-secret-key
PAYSTACK_PUBLIC_KEY=your-paystack-public-key
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_HOST_USER=your-email@gmail.com
EMAIL_HOST_PASSWORD=your-app-password
CORS_ORIGINS=http://localhost:5173,http://localhost:80
```

## Subscription Plans

The admin can create subscription plans via the `/admin/plans` endpoint. Each plan defines:
- Monthly/Yearly billing
- Max products limit
- Max users limit
- Features description
- Active/inactive status

Users can subscribe to plans via the mobile app with Paystack payment integration.

## Project Structure

```
stock-mgmt-app/
├── api/                           # Spring Boot Backend
│   └── src/main/java/com/stockmgmt/api/
│       ├── config/                # Security, JWT, Paystack config
│       ├── controller/            # REST endpoints
│       ├── entity/                # JPA entities, DTOs
│       ├── repository/            # Data access
│       ├── service/               # Business logic
│       └── exception/             # Error handling
├── app/                           # React Mobile Frontend
│   └── src/
│       ├── pages/                 # Route pages
│       ├── components/            # Reusable components
│       ├── store/                 # Zustand state
│       ├── services/              # API client
│       ├── types/                 # TypeScript interfaces
│       └── constants/             # App constants
├── payguard-docker/               # Docker compose files
│   ├── docker-compose.yml
│   └── docker-compose.db.yml
└── README.md
```

## API Endpoints

### Auth
- `POST /api/v1/auth/register` - Register user + store
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token
- `GET /api/v1/auth/me` - Current user

### Products
- `POST /api/v1/stores/{storeId}/products` - Create product
- `GET /api/v1/stores/{storeId}/products` - List products
- `PUT /api/v1/stores/{storeId}/products/{id}` - Update product
- `DELETE /api/v1/stores/{storeId}/products/{id}` - Delete product

### Sales
- `POST /api/v1/stores/{storeId}/sales` - Create sale
- `GET /api/v1/stores/{storeId}/sales` - List sales
- `GET /api/v1/stores/{storeId}/dashboard/stats` - Dashboard stats

### Subscriptions
- `GET /api/v1/subscriptions/plans` - Available plans
- `GET /api/v1/subscriptions/current` - Current subscription
- `POST /api/v1/subscriptions/initiate` - Initiate Paystack payment
- `POST /api/v1/subscriptions/verify` - Verify payment

### Admin
- `POST /api/v1/admin/plans` - Create plan
- `GET /api/v1/admin/plans` - List plans
- `PUT /api/v1/admin/plans/{id}` - Update plan
- `DELETE /api/v1/admin/plans/{id}` - Delete plan
- `GET /api/v1/admin/users` - List users# stockplus
