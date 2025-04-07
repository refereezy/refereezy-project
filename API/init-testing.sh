docker build . -t api-app:test &&
cd test-db &&
docker build . -t test-db:latest &&
docker compose down &&
echo "y" | docker image prune &&
docker compose up -d