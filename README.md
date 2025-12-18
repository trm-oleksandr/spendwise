# SpendWise

JavaFX personal finance tracker for course project. Uses PostgreSQL, JDBC (Spring JdbcTemplate), and Docker for local setup.

## Prerequisites
- JDK 21
- Apache Maven 3.9+
- Docker + Docker Compose (for PostgreSQL)

## Quick start (as required by the plan)
1. Start the database:
   ```bash
   docker compose up -d
   ```
   This launches PostgreSQL 16 with credentials `spendwise/spendwise` and runs `init.sql` to create tables.
2. Verify the database is healthy (optional):
   ```bash
   docker compose logs --tail=50
   ```
3. Run the JavaFX app from Maven:
   ```bash
   mvn clean javafx:run
   ```

## Configuration
- Database connection is read from `src/main/resources/db.properties`:
  ```properties
  db.url=jdbc:postgresql://localhost:5432/spendwise
  db.user=spendwise
  db.password=spendwise
  ```
  Adjust these values if you run PostgreSQL on a different host/port or with other credentials.

## Useful commands
- Run unit tests: `mvn test`
- Build without launching the UI: `mvn package`
- Stop database containers: `docker compose down`

## Notes
- Main class: `sk.upjs.ics.spendwise.App` (JavaFX). IDE users can run `sk.upjs.ics.spendwise.IDELauncher`.
- UI resources are under `src/main/resources/ui` (FXML) with styles in `src/main/resources/ui/style.css`.
