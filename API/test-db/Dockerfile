# Use the official PostgreSQL image as the base image
FROM postgres:latest

# Set the working directory inside the container
WORKDIR /docker-entrypoint-initdb.d

# Add a script to create the database and switch to it
COPY 01-init-db.sql /docker-entrypoint-initdb.d/

# Copy the SQL scripts to the container
COPY 02-dml.sql /docker-entrypoint-initdb.d/

# The scripts in /docker-entrypoint-initdb.d/ will be automatically executed
# by the PostgreSQL image during initialization.

# Expose the default PostgreSQL port
EXPOSE 5432