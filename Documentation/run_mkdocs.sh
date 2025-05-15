sudo docker build -t mi-mkdocs .
sudo docker run -p 8000:8000 -v $(pwd):/docs mi-mkdocs
sudo docker-compose up -d