# Garbage

_Provides information on when garbage collection takes place at a specific address or within a certain area._

## Getting Started

### Prerequisites

- **Java 21 or higher**
- **Maven**
- **MariaDB**
- **Git**
- **SFTP** (to fetch garbage schedule from)

### Installation

1. **Clone the repository:**

   ```bash
   git clone git@github.com:Sundsvallskommun/api-service-garbage.git
   ```

2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#Configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible. See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

     ```bash
     mvn spring-boot:run
     ```

## Dependencies

- **SFTP Server**

  - **Purpose:** The service relies on an SFTP server to find the `schedule.csv` file to populate the database with.
  - **Setup Instructions:** Refer to [Configuration](#configuration) for instructions on how to configure this application to talk to your SFTP server.

  Alternatively, it is possible to disable the scheduled job and populate the database manually.

Ensure that these services are running and properly configured before starting this microservice.

## API Documentation

Access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

Alternatively, refer to the `openapi.yml` file located in the project's root directory for the OpenAPI specification.

## Usage

### API Endpoints

Refer to the [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Request

```bash
curl -X GET http://localhost:8080/api/resource
```

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in `application.yml`.

### Key Configuration Parameters

- **Server Port:**

  ```yaml
  server:
    port: 8080
  ```

- **Database Settings:**

  ```yaml
  spring:
    datasource:
      url: jdbc:mariadb://localhost:3306/your_database
      username: your_db_username
      password: your_db_password
  ```

- **Scheduler Settings:**

  ```yaml
  schedulers:
    update-garbage-schedules:
      municipality-ids: 2281
      cron: 0 0 5 * * MON-FRI
      shedlock-lock-at-most-for: PT2M
  ```
  - **Municipality-ids:** a list for which municipalityIds to run the scheduled job for. Will use the same SFTP settings for each one.
  - **Cron:** When scheduled job should run. Use `"-"`to disable.

- **SFTP settings:**

  ```yaml
  integration:
    sftp:
      username: your_sftp_username
      password: your_sftp_password
      remote-host: your-sftp-host
      filename: schedule.csv
  ```

### Database Initialization

The project is set up with [Flyway](https://github.com/flyway/flyway) for database migrations. Flyway is disabled by default so you will have to enable it to automatically populate the database schema upon application startup.

```yaml
spring:
  flyway:
    enabled: true
```

- **No additional setup is required** for database initialization, as long as the database connection settings are correctly configured.

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Code status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-garbage&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-garbage)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-garbage&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-garbage)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-garbage&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-garbage)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-garbage&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-garbage)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-garbage&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-garbage)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-garbage&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-garbage)

---

© 2024 Sundsvalls kommun
