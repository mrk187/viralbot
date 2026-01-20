#!/bin/bash

echo "Starting PostgreSQL..."
docker-compose up -d

echo "Building frontend..."
cd frontend && npm run build && cd ..

echo "Copying to static..."
rm -rf src/main/resources/static
mkdir -p src/main/resources/static
cp -r frontend/dist/* src/main/resources/static/

echo ""
echo "✅ Setup complete!"
echo ""
echo "Now run the app from IntelliJ:"
echo "1. Open ViralBotApplication.java"
echo "2. Click the green ▶ button"
echo "3. Open http://localhost:8080"
echo ""
