<h1 style="text-align: center;">backend_staffoji_Game</h1>

<p align="center">
  <img src="https://img.shields.io/badge/java-v17-blue.svg" />
  <img src="https://img.shields.io/badge/maven-v3.6.3-blue.svg" />
  <img src="https://img.shields.io/badge/spring--boot-v2.3.4-blue.svg" />
  <img src="https://img.shields.io/badge/postgresql-v12-blue.svg" />
  <img src="https://img.shields.io/badge/liquibase-v4.3.5-blue.svg" />
  <img src="https://img.shields.io/badge/docker-v19.03.12-blue.svg" />
  <img src="https://img.shields.io/badge/oauth2-blue.svg" />
  <img src="https://img.shields.io/badge/swagger--ui-v3.25.0-blue.svg" />
  <img src="https://img.shields.io/badge/python-v3.8-blue.svg" />
  <img src="https://img.shields.io/badge/azure--pipelines-blue.svg" />
  <img src="https://img.shields.io/badge/junit-v5.6.2-blue.svg" />
  <img src="https://img.shields.io/badge/mockito-v3.3.3-blue.svg" />
  <img src="https://img.shields.io/badge/java--mail--sender-blue.svg" />
</p>
<h3>Welcome to the backend for Staffoji Game! 
This repository contains the server-side code for managing user data,
notifications, leaderboard, security, and more, all implemented in Java.
Additionally, Python scripts are integrated for database backup operations,
adding an extra layer of resilience to the system.</h2>

## Video Explanation
To get a quick overview of the project, watch this explainer video:

[![Explainer Video](http://img.youtube.com/vi/V8_FROSzBKg/0.jpg)](http://www.youtube.com/watch?v=V8_FROSzBKg "Explainer Video")


## Features:
- **User Backend:** Manages user accounts, authentication, and user-related data.
- **Notification System:** Implements a notification system to keep users informed about important events. It offers alternatives to send notifications immediately or schedule them for the future.
- **Leaderboard:** Provides functionality to track and display the top players.
- **Security:** Security is ensured through OAuth2 login on Swagger UI.
- **Verification Email:** 2 step verification email, first is to verify the email and second is to send verification link to the user email
- **Database:** Saving data with Liquibase in postgresSQL database because more flexibility of database.
- **Backup:** Includes Python code to send the user database in Excel format via email.
- **Java Code:** All functionalities are implemented using the Java programming language.
- **Testing:** Includes tests to ensure the reliability and correctness of the implemented features.
- **Preparing Environments:** Supports local, dev, and prod environments, and is ready for CI/CD on Azure Pipelines.



## Technologies:
```
- **Java:** The primary programming language used for the backend.
- **Maven:** Used for project management and dependency management.
- **Spring Boot:** Framework for building the backend application.
- **PostgreSQL:** Database used for storing user data.
- **Liquibase:** Database schema change management.
- **Docker:** Containerization tool used for creating and managing containers.
- **OAuth2:** Used for secure authentication.
- **Swagger UI:** Provides a user interface for interacting with the API.
- **Python:** Used for backup scripts to export the database to Excel and send via email.
- **Azure Pipelines:** CI/CD tool used for continuous integration and deployment.
- **JUnit:** Testing framework used for writing and running tests.
- **Mockito:** Mocking framework used for writing and running tests.
- **JavaMailSender with gmail:** Used for sending emails.
```
## Installation:

Local installation requires the following steps:

1. Start a PostgreSQL container:
   ```bash
   docker run -d --name BoostApp -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres
   
2. Run docker image:
3. Set the following environment variables to `local`:

   - `APP_PASSWORD`: Password for the email account used by the application to send emails. Replace this with your actual email account password.
   - `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`: Google OAuth2 client ID and client secret. Replace these with your actual credentials.
   - `ALLOWED_EMAILS`: Comma-separated list of emails allowed to use the application. Replace with actual emails.
4. Run the application or click on play button:
   ```bash
   mvn spring-boot:run
   ```

5. Access Swagger UI: http://localhost:8083/swagger-ui.html


## Usage:

- Ensure the PostgreSQL database is running and accessible.
- Set up the required environment variables for your local, dev, or prod setup.
- Build and run the application.
- Use the Swagger UI to interact with the API endpoints and test the features.

## License

[MIT](https://choosealicense.com/licenses/mit/)