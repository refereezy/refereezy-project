FROM node:18-alpine

# Create app directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy tsconfig
COPY tsconfig.json ./

# Copy source code
COPY . .

# Build TypeScript code
RUN npm run build

# Expose port
EXPOSE 3000

# Start the application
ENTRYPOINT ["npm", "run", "dev"]

