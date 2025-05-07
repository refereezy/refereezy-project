FROM node:18-alpine

# Create app directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies (including dev dependencies)
RUN npm install --include=dev
# Install ts-node-dev globally to ensure it's in PATH
RUN npm install -g ts-node-dev
# Install necessary type definitions
RUN npm install --save-dev @types/node @types/express @types/cors

# Copy tsconfig
COPY tsconfig.json ./

# Copy source code
COPY . .

# Build TypeScript code
# No need to build if using ts-node-dev, but uncomment if you want to build
# RUN npm run build

# Expose port
EXPOSE 3000

# Start the application
CMD ["npm", "run", "dev"]

