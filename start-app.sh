#!/bin/bash

echo "Starting StockPulse Development Environment..."

# Start Backend
echo "Starting Backend..."
cd api

if [ -f .env ]; then
    echo "Loading .env"
    set -a
    source .env
    set +a
fi

mvn spring-boot:run &
BACKEND_PID=$!
cd ..

# Start Frontend
echo "Starting Frontend..."
cd app
pnpm dev &
FRONTEND_PID=$!
cd ..

echo ""
echo "=========================================="
echo "  StockPulse is starting..."
echo "  Backend: http://localhost:8080"
echo "  Frontend: http://localhost:5173"
echo "=========================================="
echo ""

# Cleanup on exit
trap "kill $BACKEND_PID $FRONTEND_PID 2>/dev/null" EXIT
wait