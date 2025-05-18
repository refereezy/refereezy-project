# Usa la imagen oficial de Python
FROM python:3.11

# Instala git y dependencias básicas
RUN apt-get update && apt-get install -y git && apt-get clean

# Establece el directorio de trabajo
WORKDIR /docs

# Copia los requisitos de Python
COPY requirements.txt .

# Instala las dependencias de Python
RUN pip install --no-cache-dir -r requirements.txt

# Copia todo el proyecto
COPY . .

# Expone el puerto
EXPOSE 8000

# Comando por defecto
CMD ["mkdocs", "serve", "-a", "0.0.0.0:8000"]
