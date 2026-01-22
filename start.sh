#!/bin/bash

echo "Checking PostgreSQL..."

if docker ps | grep -q viralbot-postgres; then
    echo "✅ PostgreSQL is already running"
else
    echo "Starting PostgreSQL..."
    docker-compose up -d
    echo "✅ PostgreSQL started"
fi

echo ""
echo "Ready! Now run the app from IntelliJ:"
echo "1. Open ViralBotApplication.java"
echo "2. Click the green ▶ button (frontend builds automatically)"
echo "3. Open http://localhost:8080"
echo ""
