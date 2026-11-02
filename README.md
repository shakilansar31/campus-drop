# CampusDrop - Lost & Found Management System

A college mini-project built using Core Java, Java Collections Framework, and Oracle SQL Database via JDBC.

## Features
- Report and search lost and found items across campus
- Claim verification queue for student ownership proof
- Audit history log for returned items
- Interactive web dashboard and alternative console CLI

## Data Structures Used
- `HashMap<Integer, Item>`: In-memory item lookup and caching
- `LinkedList<Item>`: Sequential audit log of returned items
- `Queue<ClaimRequest>`: FIFO queue for processing student claim requests
- `Stack<String>`: LIFO stack for undoing operations

## Tech Stack
- **Language**: Java (JDK 8+)
- **Database**: Oracle Database (via `ojdbc8.jar`)
- **Frontend**: HTML, CSS, JavaScript (served by pure Java `HttpServer`)

## How to Run

### 1. Compile
```bash
javac -cp "lib/ojdbc8.jar;src" -d bin src/model/*.java src/database/*.java src/*.java
```

### 2. Run Web Dashboard
```bash
java -cp "bin;lib/ojdbc8.jar" WebServer
```
Open [http://localhost:3000](http://localhost:3000) in your browser.

### 3. Run Console Interface (Optional)
```bash
java -cp "bin;lib/ojdbc8.jar" Main
```
