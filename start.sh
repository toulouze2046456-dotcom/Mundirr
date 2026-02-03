#!/bin/bash
# Fast start script for Vellbeing OS

cd "$(dirname "$0")"

# Kill existing processes
lsof -ti:8080 | xargs kill -9 2>/dev/null
lsof -ti:5173 | xargs kill -9 2>/dev/null

echo "🚀 Starting Vellbeing OS..."

# Start backend (skip tests, use daemon mode)
echo "📦 Backend starting on :8080..."
mvn spring-boot:run -DskipTests -Dspring-boot.run.fork=false > /tmp/backend.log 2>&1 &

# Start frontend
echo "🎨 Frontend starting on :5173..."
cd client && npm run dev > /tmp/frontend.log 2>&1 &

echo "✅ Both services starting!"
echo "   Backend: http://localhost:8080"
echo "   Frontend: http://localhost:5173"
echo ""
echo "Logs: /tmp/backend.log, /tmp/frontend.log"
