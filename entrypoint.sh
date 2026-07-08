#!/bin/sh

set -e

echo "Starting Redis..."
redis-server --bind 127.0.0.1 --protected-mode yes &

echo "Waiting Redis..."
sleep 2

echo "Starting Spring Boot..."
exec java -jar app.jar