# Usa la imagen oficial de Python
FROM python:3.11-slim

# Establece el directorio de trabajo
WORKDIR /docs

# Copia el archivo de requerimientos (en este caso solo mkdocs)
COPY requirements.txt .

# Instala MkDocs
RUN pip install -r requirements.txt

# Copia todo el contenido del proyecto (docs, mkdocs.yml, etc)
COPY . .

# Expone el puerto donde corre MkDocs (por defecto 8000)
EXPOSE 8000

# Comando para iniciar el servidor de MkDocs en modo no interactivo y sin recarga automática
CMD ["mkdocs", "serve", "-a", "0.0.0.0:8000"]
