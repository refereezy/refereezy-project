FROM node:18-alpine

# Create app directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies (including dev dependencies)
RUN npm install --include=dev
# Install ts-node-dev globally to ensure it's in PATH
RUN npm install -g ts-node-dev

# Copy tsconfig
COPY tsconfig.json ./

# Copy source code
COPY . .

# Build TypeScript code
RUN npm run build

# Expose port
EXPOSE 3000

# Start the application - changing from ENTRYPOINT to CMD
CMD ["npm", "run", "dev"]

