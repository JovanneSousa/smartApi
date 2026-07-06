#!/bin/bash

set -e

echo "Starting Redis..."

redis-server \
    --bind 127.0.0.1 \
    --protected-mode yes &

REDIS_PID=$!

echo "Redis started with PID $REDIS_PID"


echo "Starting Java application..."

java -jar app.jar &

JAVA_PID=$!

function shutdown() {
    echo "Stopping services..."

    kill $JAVA_PID
    kill $REDIS_PID

    wait
}

trap shutdown SIGTERM SIGINT


wait $JAVA_PID