# CAMPUS DROP: Smart Lost & Found Management System

> **A College-Level Java Mini-Project Using Standard Data Structures & Local Oracle SQL Database**

---

## 📌 Project Overview
**CampusDrop** is a smart Lost and Found Management System designed for college campuses. It combines standard Java Data Structures (**HashMap**, **LinkedList**, **Stack**, **Queue**) for in-memory runtime operations with a **Local Oracle SQL Database** (via JDBC DAOs) for persistent storage.

---

## 🛠️ Data Structures & Database Architecture

```text
                           CampusDrop Application
                                      |
       +------------------------------+------------------------------+
       |                                                             |
   Java DSA Layer (Runtime)                                 Oracle JDBC Layer (Persistence)
   -----------------------                                 ------------------------------
   - HashMap<Integer, Item>   <---- Sync / Cache ---->      - ITEMS Table
   - LinkedList<Item>         <---- Audit Trail ---->      - RECOVERY_HISTORY Table
   - Queue<ClaimRequest>      <---- FIFO Queue  ---->      - CLAIMS Table
   - Stack<String>            <---- LIFO Undo   ---->      - PreparedStatement DAOs
```

| Component | Class / Object | Purpose in System |
| :--- | :--- | :--- |
| **HashMap** | `HashMap<Integer, Item>` | Stores all items using unique **Item ID** as key ($O(1)$ lookup time). |
| **LinkedList** | `LinkedList<Item>` | Stores history of returned items (`recoveryHistory`) in chronological sequence. |
| **Stack** | `Stack<String>` | Implements **Undo Action** functionality (`actionStack`) following LIFO mechanics. |
| **Queue** | `Queue<ClaimRequest>` | Manages pending item claim requests (`claimQueue`) in strict **FIFO** sequence. |
| **Oracle DB** | `ITEMS`, `CLAIMS`, `RECOVERY_HISTORY` | Permanent database layer accessed using Oracle JDBC Driver (`ojdbc8.jar`). |

---

## 📁 Project Structure

```text
CampusDrop/
├── src/
│   ├── Main.java               # Console menu driver
│   ├── WebServer.java          # Pure Java HTTP server on http://localhost:3000
│   ├── CampusDrop.java         # Business logic synchronizing Java Collections & DAOs
│   ├── DatabaseTest.java       # Diagnostic JDBC connection test harness
│   │
│   ├── model/
│   │   ├── Item.java           # Model class for Lost and Found items
│   │   └── ClaimRequest.java   # Model class for student claim requests
│   │
│   └── database/
│       ├── DatabaseConnection.java # Configurable Oracle JDBC connection settings
│       ├── ItemDAO.java        # PreparedStatement DAO for ITEMS table
│       ├── ClaimDAO.java       # PreparedStatement DAO for CLAIMS table
│       └── HistoryDAO.java     # PreparedStatement DAO for RECOVERY_HISTORY table
│
├── sql/
│   ├── campus_drop.sql         # Oracle SQL schema creation & sample data script
│   └── queries.sql             # SQL test queries for Oracle SQL Developer
│
├── lib/
│   └── ojdbc8.jar              # Oracle JDBC Driver JAR
│
├── index.html                  # Neo-Brutalism Dashboard Web UI
├── oop.html                    # OOP & DSA Architecture Showcase Page
├── README.md                   # Installation, build, & usage documentation
├── PROJECT_REPORT.md           # 28-Section Academic Mini-Project Report
└── VIVA.md                     # 30+ Viva Voce questions and answers
```

---

## ⚙️ Local Oracle Database Setup

### Step 1: Install Local Oracle Database
Ensure Oracle Database 21c XE, 19c, or 18c is installed and running on your machine.

### Step 2: Start Oracle Services
In Windows Services, make sure the Oracle database service and listener installed by your Oracle version are running. Service names vary by version and installation.
```powershell
Get-Service *Oracle*
```

### Step 3: Run Oracle SQL Setup Script
Execute `sql/campus_drop_safe.sql` in the `XEPDB1` pluggable database using Oracle SQL Developer or SQL*Plus. It creates missing CampusDrop tables, sequences, and indexes, then inserts demo item rows only when their IDs are absent. It preserves existing rows. The older `campus_drop.sql` file is a destructive reset script and drops its tables.
```bash
sqlplus <username>/<password>@localhost:1522/XEPDB1 @sql/campus_drop_safe.sql
```

---

## ⚙️ Configuring Database Credentials

Connection details are loaded from `campusdrop.properties` in the project root. The detected Oracle XE 21c installation on this laptop is configured for `127.0.0.1:1522` with service `XEPDB1`; the local file is already set to these values. Add the password for the database user that owns the CampusDrop tables:

```properties
db.host=localhost
db.port=1522
db.service=XEPDB1
db.username=system
db.password=your-local-password
```

Use the service name shown by Oracle if it differs from `XEPDB1`. You can instead set `db.url` to a full JDBC URL. Environment variables `CAMPUSDROP_DB_HOST`, `CAMPUSDROP_DB_PORT`, `CAMPUSDROP_DB_SERVICE`, `CAMPUSDROP_DB_USERNAME`, `CAMPUSDROP_DB_PASSWORD`, and `CAMPUSDROP_DB_URL` override file settings. The password is no longer stored in Java source.

---

## 💻 Compilation & Running Instructions

### Step 1: Open Terminal
```powershell
cd "C:\Users\shaki\OneDrive\Desktop\Campus IDEA"
```

### Step 2: Test Oracle Database Connection
```powershell
javac -cp "lib/ojdbc8.jar;src" src/model/*.java src/database/*.java src/*.java
java -cp "lib/ojdbc8.jar;src" DatabaseTest
```

### Step 3: Run Interactive Console Application
```powershell
java -cp "lib/ojdbc8.jar;src" Main
```

### Step 4: Run Web UI Server on `http://localhost:3000`
```powershell
java -cp "lib/ojdbc8.jar;src" WebServer
```
Open your browser to:
- **Dashboard**: [http://localhost:3000](http://localhost:3000)
- **OOP Architecture Guide**: [http://localhost:3000/oop](http://localhost:3000/oop)
# campus-drop
