#!/bin/bash

cd test-db &&
sudo docker-compose down &&
sudo docker image prune -a &&
sudo docker volume prune &&
sudo docker build . -t test-db:latest &&
cd .. &&
sudo docker build . -t api-app:test &&
cd test-db &&
sudo docker-compose up -d
