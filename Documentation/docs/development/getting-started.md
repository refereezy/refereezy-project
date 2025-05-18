# Getting Started with Development

## Introduction

This guide will help you set up your development environment to work on the Refereezy project. The project consists of several components that work together to provide a comprehensive sports management platform.

## Prerequisites

Before you begin, make sure you have the following software installed:

- [Docker](https://www.docker.com/get-started) and [Docker Compose](https://docs.docker.com/compose/install/)
- [Node.js](https://nodejs.org/) (v16 or higher)
- [Python](https://www.python.org/) (v3.9 or higher)
- [Git](https://git-scm.com/)

## Quick Start

1. **Clone the Repository**

   ```bash
   git clone https://github.com/refereezy/refereezy-project.git
   cd refereezy-project
   ```

2. **Set Up Environment Variables**

   Create `.env` files in the appropriate directories as described in the project's component documentation.

3. **Run the Development Environment**

   Use our Docker Compose scripts to start all necessary services:

   ```bash
   # On Windows
   cd scripts
   .\rebuild_all_containers.bat
   
   # On Linux/Mac
   cd scripts
   ./rebuild_all_containers.sh
   ```

4. **Access Development Services**

   - Documentation: [http://localhost:8000](http://localhost:8000)
   - API: [http://localhost:8080](http://localhost:8080)
   - Test API: [http://localhost:8888](http://localhost:8888)
   - Socket Server: [http://localhost:3000](http://localhost:3000)

## Component Development

The Refereezy project consists of multiple components. Please refer to specific documentation for each component:

- [API Development](environment-setup.md#api-development)
- [Web Application Development](environment-setup.md#web-application-development)
- [Mobile Application Development](environment-setup.md#mobile-application-development)
- [Watch Application Development](environment-setup.md#watch-application-development)

## Troubleshooting

If you encounter issues during the setup process, refer to the [Troubleshooting Guide](environment-setup.md#troubleshooting) or check the [Docker Containers](docker.md) documentation.

---

*Note: Add detailed setup steps, environment configurations, and common problems/solutions here. Include screenshots of successful setup states and explain each component's development workflow.*
