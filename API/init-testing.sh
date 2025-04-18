#!/bin/bash

cd test-db &&
docker compose down &&
echo "y" | docker image prune -a &&
docker build . -t test-db:latest &&
cd .. &&
docker build . -t api-app:test &&
cd test-db &&
docker compose up -d