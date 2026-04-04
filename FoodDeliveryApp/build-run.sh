#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
#  build-run.sh  –  Build and run the Food Delivery App
#  Usage:  ./build-run.sh         (build + run)
#          ./build-run.sh run     (run already-built JAR)
#          ./build-run.sh clean   (clean build artifacts)
# ─────────────────────────────────────────────────────────────────────────────
set -e

JAR="target/food-delivery-app-jar-with-dependencies.jar"

if [[ "$1" == "clean" ]]; then
    mvn clean
    echo "Cleaned."
    exit 0
fi

if [[ "$1" != "run" ]]; then
    echo "Building Food Delivery App..."
    mvn clean package -q
    echo "Build successful → $JAR"
fi

echo "Starting Food Delivery App..."
echo "API will be available at: http://localhost:8080/api/"
echo ""
java -jar "$JAR"
