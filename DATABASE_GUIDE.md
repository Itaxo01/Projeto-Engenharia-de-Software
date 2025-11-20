# Database Configuration Guide

## Overview

This project supports **two database configurations**:
- **H2** (default) - In-memory/file-based database for local development
- **PostgreSQL** - Production database with web interfaces for management

## Quick Start

### Default Admin User

**🔑 On first startup, a default admin account is automatically created:**

- **Email:** `admin@admin.com`
- **Password:** `admin123`
- **⚠️ IMPORTANT:** Change this password after first login!

This eliminates the need for manual database access to set admin privileges.

### Development (H2 - Default)

```bash
# Just run the application - no setup needed!
mvn spring-boot:run
```

**Access H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/academic-system`
- Username: `sa`
- Password: (leave empty)

### Production Testing (PostgreSQL)

```bash
# Start PostgreSQL and pgAdmin
sudo docker compose up -d postgres pgadmin

# Run application with production profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**Access pgAdmin (PostgreSQL Web Interface):**
- URL: http://localhost:5050
- Email: `admin@admin.com`
- Password: `admin`

**To connect to PostgreSQL in pgAdmin:**
1. Open http://localhost:5050
2. Click "Add New Server"
3. General tab: Name = `Academic System`
4. Connection tab:
   - Host: `postgres` (or `localhost` if connecting from host machine)
   - Port: `5432`
   - Database: `academic_system`
   - Username: `postgres`
   - Password: `postgres`
5. Click Save

## Database Interfaces

### H2 Console (Development)
- **URL:** http://localhost:8080/h2-console
- **Built-in:** Enabled automatically in development mode
- **Features:**
  - Browse tables and data
  - Execute SQL queries
  - View database schema
  - Export data

### pgAdmin (PostgreSQL)
- **URL:** http://localhost:5050
- **Requires:** Docker Compose
- **Features:**
  - Visual query builder
  - Database dashboard
  - Query history
  - Data import/export
  - Backup/restore
  - User management

### Alternative: psql Command Line

```bash
# Connect to PostgreSQL
psql -U postgres -d academic_system

# Useful commands:
\dt              # List all tables
\d table_name    # Describe table structure
\q               # Quit
```

## Configuration Details

### Environment Variables

| Variable | Default (Dev/H2) | Production (PostgreSQL) |
|----------|------------------|-------------------------|
| `DATABASE_URL` | `jdbc:h2:file:./data/academic-system` | `jdbc:postgresql://localhost:5432/academic_system` |
| `DATABASE_USERNAME` | `sa` | `postgres` |
| `DATABASE_PASSWORD` | (empty) | `postgres` |
| `DATABASE_DRIVER` | `org.h2.Driver` | `org.postgresql.Driver` |
| `DATABASE_DIALECT` | `org.hibernate.dialect.H2Dialect` | `org.hibernate.dialect.PostgreSQLDialect` |

### Profiles

**Development (default):** Uses H2
```bash
mvn spring-boot:run
```

**Production:** Uses PostgreSQL
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**Custom environment:**
```bash
export DATABASE_URL=jdbc:postgresql://your-host:5432/your-db
export DATABASE_USERNAME=your-user
export DATABASE_PASSWORD=your-password
mvn spring-boot:run
```

## Docker Compose Services

### Start All Services
```bash
sudo docker compose up -d
```

### Individual Services

**PostgreSQL only:**
```bash
sudo docker compose up -d postgres
```

**pgAdmin only:**
```bash
sudo docker compose up -d pgadmin
```

**Application with PostgreSQL:**
```bash
sudo docker compose up -d app
```
Access app at: http://localhost:8081 (note different port to avoid conflict with local dev)

### Stop Services
```bash
sudo docker compose down
```

### Clean All Data
```bash
sudo docker compose down -v  # WARNING: Deletes all database data!
```

## Schema Management

### Automatic Schema Creation

Both H2 and PostgreSQL will **automatically create tables** based on your JPA entities:
- `usuarios`
- `professores`
- `disciplinas`
- `avaliacoes`
- `comentarios`
- `arquivos_comentario`
- `mapa_curricular`
- `scrapper_status`
- Join tables (`professor_disciplina`, `comentario_votes`)

Controlled by: `spring.jpa.hibernate.ddl-auto=update`

### Manual SQL (if needed)

**H2 Console:**
1. Go to http://localhost:8080/h2-console
2. Click "Connect"
3. Enter SQL in the text area
4. Click "Run"

**pgAdmin:**
1. Go to http://localhost:5050
2. Connect to server
3. Right-click database → "Query Tool"
4. Write SQL and click Execute (F5)

**psql:**
```bash
psql -U postgres -d academic_system -c "SELECT * FROM usuarios;"
```

## Common SQL Queries

```sql
-- View all tables
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public';

-- Count records
SELECT COUNT(*) FROM usuarios;

-- View recent comments
SELECT * FROM comentarios ORDER BY created_at DESC LIMIT 10;

-- Check professor-discipline relationships
SELECT p.nome, d.nome 
FROM professores p 
JOIN professor_disciplina pd ON p.professor_id = pd.professor_id
JOIN disciplinas d ON d.disciplina_id = pd.disciplina_id;

-- Reset database (CAUTION!)
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
```

## Troubleshooting

### "Table not found" error
- Stop the app and delete `./data/` folder, then restart
- For PostgreSQL: Run `DROP SCHEMA public CASCADE; CREATE SCHEMA public;`

### Can't connect to H2 Console
- Make sure `spring.h2.console.enabled=true` in application.properties
- Check the JDBC URL matches exactly: `jdbc:h2:file:./data/academic-system`

### Can't connect to PostgreSQL
```bash
# Check if PostgreSQL is running
sudo docker compose ps

# View logs
sudo docker compose logs postgres

# Restart PostgreSQL
sudo docker compose restart postgres
```

### pgAdmin won't start
```bash
# Check port 5050 availability
sudo lsof -i :5050

# View pgAdmin logs
sudo docker compose logs pgadmin
```

### "password authentication failed"
- Make sure you set the password: `sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'postgres';"`
- Or use environment variables to override defaults

## Data Persistence

### H2 (File-based)
- Location: `./data/academic-system.mv.db`
- Persists between restarts
- **To reset:** Delete `./data/` folder

### PostgreSQL (Docker volume)
- Managed by Docker
- Persists between container restarts
- **To reset:** `sudo docker compose down -v`

### PostgreSQL (System installation)
- Location: `/var/lib/postgresql/data/`
- Managed by PostgreSQL service
- **To reset:** Use SQL commands or `dropdb` command

## Production Deployment

### Using Docker (Recommended)

Your Dockerfile is already configured. Deploy with:

```bash
docker build -t academic-system .
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://your-db-host:5432/your-db \
  -e DATABASE_USERNAME=your-user \
  -e DATABASE_PASSWORD=your-password \
  academic-system
```

### Platform-Specific (Render, Heroku, AWS, etc.)

1. Add PostgreSQL addon/service
2. Set environment variables (usually auto-configured):
   - `DATABASE_URL`
   - `DATABASE_USERNAME`  
   - `DATABASE_PASSWORD`
3. Deploy your application

Most platforms automatically detect Spring Boot and run it correctly.

## Switching Between Databases

### H2 → PostgreSQL
1. Start PostgreSQL: `sudo docker compose up -d postgres`
2. Run with prod profile: `mvn spring-boot:run -Dspring-boot.run.profiles=prod`

### PostgreSQL → H2
1. Stop using prod profile (just run `mvn spring-boot:run`)
2. H2 automatically activated

### Keep Both Running
- H2: Default on port 8080
- PostgreSQL via Docker: App on port 8081
```bash
# Terminal 1: H2 development
mvn spring-boot:run

# Terminal 2: PostgreSQL testing
sudo docker compose up app
```

## Performance Tips

### For Development (H2)
- Keep `spring.jpa.show-sql=true` to debug queries
- Data resets on application restart (in-memory mode)

### For Production (PostgreSQL)
- Connection pooling is configured in `application-prod.properties`
- Monitor with pgAdmin dashboard
- Set `spring.jpa.show-sql=false` to reduce logs

---

**Need help?** Check the logs or run with debug: `mvn spring-boot:run -Ddebug`
