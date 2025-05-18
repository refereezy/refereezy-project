# Deployment Guide

This guide provides comprehensive instructions for deploying the Refereezy platform in various environments.

## Deployment Environments

Refereezy uses three main deployment environments:

1. **Development** - For ongoing development and testing
2. **Staging** - For pre-production testing and client reviews
3. **Production** - Live environment for end users

## Infrastructure Overview

The Refereezy platform consists of the following components that need to be deployed:

1. **API Backend** - FastAPI application with PostgreSQL database
2. **Web Application** - Next.js frontend application
3. **Firebase Services** - Real-time database and authentication
4. **Mobile Application** - Android APK distribution
5. **Watch Application** - Wear OS application distribution

## Prerequisites

Before deployment, ensure you have:

- AWS account with appropriate permissions
- Docker and Docker Compose installed
- Firebase project configured
- Domain names registered and DNS configured
- SSL certificates obtained (Let's Encrypt recommended)
- Access to all relevant code repositories

## API Backend Deployment

The API backend is deployed using Docker containers on AWS EC2.

### Environment Configuration

1. Create a `.env` file for each environment with the necessary variables:

```
# Database Configuration
DB_USER=refereezy_user
DB_PASS=secure_password
DB_PORT=5432
DB_NAME=refereezy
DB_HOST=db-instance.amazonaws.com

# Firebase Configuration
FIREBASE_API_KEY=your_firebase_api_key
FIREBASE_AUTH_DOMAIN=your-project.firebaseapp.com
FIREBASE_DATABASE_URL=https://your-project.firebaseio.com
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_STORAGE_BUCKET=your-project.appspot.com
FIREBASE_MESSAGING_SENDER_ID=your_sender_id
FIREBASE_APP_ID=your_app_id

# API Configuration
API_PORT=8080
CORS_ORIGINS=https://app.refereezy.com,https://admin.refereezy.com

# Cloudinary Configuration
CLOUDINARY_NAME=your_cloudinary_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
```

### Docker Deployment

1. Build the API Docker image:

```bash
cd API
docker build -t refereezy-api:latest .
```

2. Deploy using Docker Compose:

```bash
docker-compose -f docker-compose.prod.yml up -d
```

### Manual Deployment (Alternative)

If not using Docker, follow these steps:

1. Install dependencies:

```bash
cd API/api
pip install -r requirements.txt
```

2. Run database migrations:

```bash
alembic upgrade head
```

3. Start the API server:

```bash
uvicorn main:app --host 0.0.0.0 --port 8080
```

### AWS Elastic Beanstalk Deployment (Optional)

For scalable deployments, you can use AWS Elastic Beanstalk:

1. Install the EB CLI:

```bash
pip install awsebcli
```

2. Initialize Elastic Beanstalk in the API directory:

```bash
eb init
```

3. Create an environment and deploy:

```bash
eb create refereezy-api-prod
```

4. For subsequent deployments:

```bash
eb deploy
```

## Web Application Deployment

The web application is built with Next.js and deployed to AWS S3 with CloudFront distribution.

### Building the Web Application

1. Install dependencies:

```bash
cd APPS/web
npm install
```

2. Create environment-specific `.env` files:

```
# .env.production
NEXT_PUBLIC_API_URL=https://api.refereezy.com
NEXT_PUBLIC_FIREBASE_API_KEY=your_firebase_api_key
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=your-project.firebaseapp.com
NEXT_PUBLIC_FIREBASE_DATABASE_URL=https://your-project.firebaseio.com
NEXT_PUBLIC_FIREBASE_PROJECT_ID=your-project-id
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=your-project.appspot.com
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
NEXT_PUBLIC_FIREBASE_APP_ID=your_app_id
```

3. Build the application:

```bash
npm run build
```

### Deploying to AWS S3 and CloudFront

1. Create an S3 bucket:

```bash
aws s3 mb s3://app.refereezy.com
```

2. Configure the bucket for static website hosting:

```bash
aws s3 website s3://app.refereezy.com --index-document index.html --error-document 404.html
```

3. Upload the build files:

```bash
aws s3 sync out/ s3://app.refereezy.com --acl public-read
```

4. Create a CloudFront distribution pointing to the S3 bucket.

5. Configure your DNS to point to the CloudFront distribution.

### Vercel Deployment (Alternative)

For simpler deployments, you can use Vercel:

1. Install Vercel CLI:

```bash
npm install -g vercel
```

2. Deploy the application:

```bash
vercel --prod
```

## Mobile Application Distribution

For distributing the Android mobile application:

### Google Play Store

1. Generate a signed APK or Android App Bundle:

```bash
cd APPS/movil/RefereezyApp
./gradlew bundleRelease
```

2. Upload the bundle to the Google Play Console.

3. Complete store listing information and roll out to the appropriate track (internal, alpha, beta, or production).

### Direct APK Distribution

For enterprise clients or direct distribution:

1. Generate a signed APK:

```bash
cd APPS/movil/RefereezyApp
./gradlew assembleRelease
```

2. Upload the APK to a secure download area on your website or distribute via enterprise MDM solutions.

## Watch Application Distribution

For distributing the Wear OS watch application:

### Google Play Store

1. Generate a signed APK or Android App Bundle:

```bash
cd APPS/reloj/RellotgeJAIS
./gradlew bundleRelease
```

2. Upload the bundle to the Google Play Console, ensuring it's properly configured for Wear OS.

3. Complete store listing information and roll out to the appropriate track.

## Database Migration

When deploying updates that include database schema changes:

1. Create migration scripts using Alembic:

```bash
cd API
alembic revision --autogenerate -m "Description of changes"
```

2. Review and adjust the generated migration script.

3. Apply migrations during deployment:

```bash
alembic upgrade head
```

## Firebase Configuration

Ensure Firebase is configured correctly across environments:

1. Create separate Firebase projects for each environment (dev, staging, prod).

2. Configure Firebase Security Rules for each project:

```json
{
  "rules": {
    "matches": {
      ".read": "auth != null",
      ".write": "auth != null && auth.token.role == 'referee'",
      "$match_id": {
        ".read": "true",
        ".write": "auth != null && auth.token.role == 'referee'"
      }
    },
    "clocks": {
      ".read": "auth != null",
      ".write": "auth != null && auth.token.role == 'referee'",
      "$clock_code": {
        ".read": "true",
        ".write": "auth != null && auth.token.role == 'referee'"
      }
    },
    "reports": {
      ".read": "auth != null",
      ".write": "auth != null && auth.token.role == 'referee'"
    }
  }
}
```

3. Update Firebase configuration in all applications when switching environments.

## SSL Certificates

All production deployments should use HTTPS with valid SSL certificates:

1. Obtain SSL certificates using Let's Encrypt:

```bash
sudo certbot certonly --standalone -d api.refereezy.com -d app.refereezy.com
```

2. Configure Nginx or other web server to use these certificates:

```nginx
server {
    listen 443 ssl;
    server_name api.refereezy.com;
    
    ssl_certificate /etc/letsencrypt/live/api.refereezy.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.refereezy.com/privkey.pem;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

3. Setup automatic renewal of certificates:

```bash
sudo certbot renew --dry-run
```

Add a cron job to renew certificates automatically:

```
0 3 * * * /usr/bin/certbot renew --quiet
```

## Continuous Integration/Continuous Deployment (CI/CD)

Set up CI/CD pipelines for automated testing and deployment:

### GitHub Actions Workflow Example

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy-api:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up Python
        uses: actions/setup-python@v2
        with:
          python-version: '3.9'
          
      - name: Install dependencies
        run: |
          cd API
          pip install -r requirements.txt
          
      - name: Run tests
        run: |
          cd API
          pytest
          
      - name: Deploy to AWS
        uses: aws-actions/configure-aws-credentials@v1
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: eu-west-1
          
      - name: Deploy with Elastic Beanstalk
        run: |
          pip install awsebcli
          cd API
          eb deploy refereezy-api-prod
          
  deploy-web:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up Node.js
        uses: actions/setup-node@v2
        with:
          node-version: '16'
          
      - name: Install dependencies
        run: |
          cd APPS/web
          npm install
          
      - name: Build application
        run: |
          cd APPS/web
          npm run build
          
      - name: Deploy to S3
        uses: aws-actions/configure-aws-credentials@v1
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: eu-west-1
          
      - name: Sync to S3
        run: |
          cd APPS/web
          aws s3 sync out/ s3://app.refereezy.com --delete
          
      - name: Invalidate CloudFront cache
        run: |
          aws cloudfront create-invalidation --distribution-id ${{ secrets.CLOUDFRONT_DISTRIBUTION_ID }} --paths "/*"
```

## Rollback Procedures

In case of deployment issues, follow these rollback procedures:

### API Rollback

1. Revert to the previous Docker image:

```bash
docker-compose -f docker-compose.prod.yml down
docker pull [repository]/refereezy-api:[previous-tag]
docker-compose -f docker-compose.prod.yml up -d
```

2. For database rollbacks, restore from the latest backup:

```bash
pg_restore -U [username] -d refereezy backup_[date].sql
```

### Web Application Rollback

1. Redeploy the previous version from S3:

```bash
aws s3 sync s3://app-backups.refereezy.com/[previous-version]/ s3://app.refereezy.com --delete
aws cloudfront create-invalidation --distribution-id [distribution-id] --paths "/*"
```

## Monitoring and Logging

After deployment, set up monitoring and logging:

### AWS CloudWatch

1. Install the CloudWatch agent on EC2 instances.

2. Configure log collection for API and web applications.

### Prometheus and Grafana (Optional)

For more advanced monitoring:

1. Deploy Prometheus for metrics collection.

2. Set up Grafana dashboards for visualization.

3. Configure alerts for critical metrics.

## Security Considerations

Ensure the following security measures are in place:

1. Keep all dependencies updated with security patches.

2. Use AWS Security Groups to restrict access to services.

3. Implement rate limiting on API endpoints.

4. Enable AWS GuardDuty for threat detection.

5. Regularly audit Firebase security rules.

6. Store all secrets in AWS Secrets Manager or similar service.

## Post-Deployment Verification

After each deployment, verify the application is functioning correctly:

1. Run automated smoke tests against the deployed API.

2. Verify user authentication flows.

3. Test real-time functionality with the watch and mobile apps.

4. Check database connections and migrations.

5. Verify SSL certificate validity and security headers.

## Documentation and Communication

After successful deployment:

1. Update release notes and changelog.

2. Notify stakeholders of the deployment completion.

3. Update documentation with any new features or configuration changes.

4. Schedule post-deployment review meeting if necessary.
