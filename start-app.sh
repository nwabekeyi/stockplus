#!/bin/bash

echo "Starting StockPulse Development Environment..."

# Start Redis
echo "Starting Redis..."
if command -v redis-server &> /dev/null; then
    redis-server --daemonize yes

    if [ $? -eq 0 ]; then
        echo "Redis started successfully."
    else
        echo "Warning: Failed to start Redis. Continuing without it..."
    fi
else
    echo "Warning: redis-server not found. Continuing without it..."
fi

# Start Backend
echo "Starting Backend..."
cd api || exit 1

export DB_URL="jdbc:postgresql://localhost:5432/stock_management?sslmode=disable"
export DB_USER="postgres"
export DB_PASSWORD="customized13"
export JWT_SECRET="stockMgmtSecretKeyChangeThisInProduction1234567890"
export JWT_ACCESS_EXPIRATION="600000"
export JWT_REFRESH_EXPIRATION="604800000"
export PAYSTACK_SECRET_KEY="sk_test_1130ff1c04a5a1a789f6c24da997d0e69d55cc46"
export PAYSTACK_PUBLIC_KEY="pk_test_8293ca0c4f5b0e4b8da1dad753cbc602cebade30"
export EMAIL_HOST="smtp.gmail.com"
export EMAIL_PORT="587"
export EMAIL_HOST_USER=""
export EMAIL_HOST_PASSWORD=""
export CORS_ORIGINS="http://localhost:5174,http://localhost:80"
export REDIS_HOST="127.0.0.1"
export REDIS_PORT="6379"
export REDIS_PASSWORD=""
export CLOUDINARY_CLOUD_NAME="dmtab5w1q"
export CLOUDINARY_API_KEY="921898848689143"
export CLOUDINARY_API_SECRET="Wb-y_fnq_WCVbnHw8atTtQLzYNw"
export CLOUDINARY_UPLOAD_FOLDER="stockpulse"

echo "Starting backend..."
mvn spring-boot:run &
BACKEND_PID=$!

cd ..

# Start Frontend
echo "Starting Frontend..."
cd app || exit 1

pnpm dev &
FRONTEND_PID=$!

cd ..

echo ""
echo "=========================================="
echo "  StockPulse is starting..."
echo "  Backend:  http://localhost:8080"
echo "  Frontend: http://localhost:5173"
echo "=========================================="
echo ""

# Cleanup on exit
trap 'kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; redis-cli shutdown nosave 2>/dev/null' EXIT

wait
