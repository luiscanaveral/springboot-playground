# Twitterlike

A proof-of-concept Twitter-like social media platform built with Spring Boot, demonstrating a microblogging system with hybrid persistence (SQLite for relational data, Neo4j for graph relationships).

## Project Description

Twitterlike implements core Twitter-like features:
- User management (creation, follow/unfollow)
- Tweet creation, liking, and commenting
- Personalized feed generation using a "fan-out" approach
- Batch processing for loading large datasets (up to 1M users)

The application uses a polyglot persistence model:
- **SQLite**: Stores relational data (users, tweets, likes, comments) via JPA/Hibernate
- **Neo4j**: Manages graph relationships (user follow connections) for efficient traversal
- **Redis**: Caches hot reads (tweets, users, likes, feeds) with 60-second TTL

Built with Spring Boot 3.2.5, Java 21, and Gradle 8.7 (with wrapper included).

## Prerequisites

- **Java 21 JDK** (required for Spring Boot 3.2.5)
- **Docker** (for running Neo4j and Redis)
- (Optional) [Task runner](https://taskfile.dev) for simplified commands via Taskfile

No local Gradle installation is required (uses included `./gradlew` wrapper).

## Running Locally

### 1. Start Databases
```bash
docker-compose up -d
```
Starts Neo4j 5.18-community on `bolt://localhost:7687` (credentials: `neo4j/password`) and Redis on port 6379.

### 2. Create Data Directory
```bash
mkdir -p .local/data
```
SQLite database will be created at `./.local/data/twitterlike.db` on first run.

### 3. Build the Project
```bash
./gradlew build -x test
```

### 4. Run the Application
```bash
./gradlew bootRun
```
Application starts on `http://localhost:8080`.

### Alternative: Using Task Runner
If Task is installed:
```bash
task docker:up  # Start Neo4j
task run        # Build and run app
```
