# Environment Setup

## Development Environments

This guide outlines how to set up your development environment for each component of the Refereezy platform.

## API Development

### Local Setup

1. **Prerequisites**
   - Python 3.9 or higher
   - PostgreSQL (or use the Docker container)

2. **Installation**
   ```bash
   cd API/api
   pip install -r requirements.txt
   ```

3. **Environment Configuration**
   Create a `.env` file in the `/API/api` directory:
   ```env
   DB_USER=your_db_user
   DB_PASS=your_db_password
   DB_PORT=5432
   DB_NAME=refereezy
   DB_HOST=localhost
   ```

4. **Running the API**
   ```bash
   cd API/api
   uvicorn main:app --reload
   ```

5. **Testing the API**
   - Access the API documentation at: http://localhost:8000/docs
   - Run the test suite: `pytest`

## Web Application Development

### Local Setup

1. **Prerequisites**
   - Node.js v16 or higher
   - npm v7 or higher

2. **Installation**
   ```bash
   cd APPS/web
   npm install
   ```

3. **Environment Configuration**
   Create a `.env` file in the `/APPS/web` directory with Firebase and API configurations.

4. **Running the Web App**
   ```bash
   cd APPS/web
   npm run dev
   ```

5. **Building for Production**
   ```bash
   npm run build
   ```

## Mobile Application Development

### Local Setup

1. **Prerequisites**
   - Android Studio for Android development
   - Xcode for iOS development (Mac only)
   - React Native environment

2. **Installation**
   ```bash
   cd APPS/movil/RefereezyApp
   npm install
   ```

3. **Running the Mobile App**
   ```bash
   # For Android
   npm run android
   
   # For iOS
   npm run ios
   ```

## Watch Application Development

### Local Setup

1. **Prerequisites**
   - Android Studio with Wear OS development tools
   - Wear OS emulator or physical device

2. **Installation and Setup**
   - Open the project in Android Studio:
     ```bash
     cd APPS/reloj/RellotgeJAIS
     ```
   - Sync Gradle dependencies
   - Configure a Wear OS emulator or connect a physical device

3. **Running the Watch App**
   - Run the app from Android Studio to a connected device or emulator

## Firebase Setup

1. **Create a Firebase Project**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project

2. **Configure Firebase Services**
   - Enable Firestore Database
   - Set up Authentication
   - Configure Storage

3. **Add Firebase to Your Applications**
   - Web: Add the provided configuration to your environment variables
   - Mobile: Download and add the google-services.json file
   - Follow Firebase documentation for each platform

## Troubleshooting

### Common Issues

#### Docker Containers
- **Issue**: Cannot start containers due to port conflicts
- **Solution**: Check if other services are using the required ports and stop them, or modify the port mappings in docker-compose.yml

#### Database Connection
- **Issue**: API cannot connect to database
- **Solution**: Verify database credentials and connection strings in .env files

#### Node.js Dependencies
- **Issue**: npm install fails with dependency errors
- **Solution**: Clear node_modules and package-lock.json, then reinstall

---

*Note: Add more detailed instructions for each platform, include configuration files templates, and add troubleshooting sections based on common issues encountered by the team.*
