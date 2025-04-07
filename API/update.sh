git pull origin main &&
sudo docker build . -t api-app:latest &&
sudo docker-compose down &&
sudo docker compose up -d &&
echo "y" | sudo docker image prune
