# CAMPUS DROP: Smart Lost & Found Management System

> **A Java & Oracle SQL Database Mini-Project using Java Collections Framework & JDBC**

---

## Project Overview
**CampusDrop** is a Lost and Found Management System designed for college campuses. It combines standard Java Data Structures (**HashMap**, **LinkedList**, **Stack**, **Queue**) for fast runtime operations with an **Oracle SQL Database** (via JDBC DAO layer) for persistent storage.

It provides an intuitive web interface at `http://localhost:3000` with Neo-Brutalist styling where students and campus staff can report lost/found items, browse items, verify claims, view potential matches, and track recovery history.

---

## Data Structures & Database Architecture

```text
                           CampusDrop Application
                                      |
       +------------------------------+------------------------------+
       |                                                             |
   Java DSA Layer (Runtime)                                 Oracle JDBC Layer (Persistence)
   -----------------------                                 ------------------------------
   - HashMap<Integer, Item>   <---- Cache / Sync ---->      - ITEMS Table
   - LinkedList<Item>         <---- Audit Trail ---->      - RECOVERY_HISTORY Table
   - Queue<ClaimRequest>      <---- FIFO Queue  ---->      - CLAIMS Table
   - Stack<String>            <---- LIFO Undo   ---->      - PreparedStatement DAOs
```

| Component | Class / Collection | Purpose in System | Time Complexity |
| :--- | :--- | :--- | :--- |
| **HashMap** | `HashMap<Integer, Item>` | Stores all items using unique **Item ID** as key. | O(1) fast lookup |
| **LinkedList** | `LinkedList<Item>` | Stores history of returned items (`recoveryHistory`) in chronological sequence. | O(1) tail appends |
| **Stack** | `Stack<String>` | Implements **Undo Action** functionality (`actionStack`) following LIFO mechanics. | O(1) push / pop |
| **Queue** | `Queue<ClaimRequest>` | Manages pending item claim requests (`claimQueue`) in strict **FIFO** sequence. | O(1) offer / poll |
| **Oracle DB** | `ITEMS`, `CLAIMS`, `RECOVERY_HISTORY` | Permanent database layer accessed using Oracle JDBC Driver (`ojdbc8.jar`). | Relational storage |

---

## Project Directory Structure

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
│       ├── DatabaseConnection.java # Oracle JDBC connection configuration
│       ├── ItemDAO.java        # PreparedStatement DAO for ITEMS table
│       ├── ClaimDAO.java       # PreparedStatement DAO for CLAIMS table
│       └── HistoryDAO.java     # PreparedStatement DAO for RECOVERY_HISTORY table
│
├── sql/
│   ├── campus_drop.sql         # Oracle SQL schema creation & initial items script
│   └── queries.sql             # SQL test queries for verification
│
├── lib/
│   └── ojdbc8.jar              # Oracle JDBC Driver
│
├── campusdrop.properties       # Database credentials & connection settings
├── index.html                  # Neo-Brutalist Web Application Dashboard
├── oop.html                    # OOP Concepts & Data Structures Guide
├── README.md                   # Project documentation
└── LICENSE                     # MIT License
```

---

## Database Configuration

Database connection settings are loaded from `campusdrop.properties` in the project root:

```properties
db.host=127.0.0.1
db.port=1522
db.service=XEPDB1
db.username=system
db.password=your_password
```

### Initializing the Database
Execute `sql/campus_drop.sql` in your Oracle Database (e.g., via Oracle SQL Developer or SQL*Plus) to create the tables, sequences, constraints, and initial sample items.

---

## Compilation & Running Instructions

### Step 1: Compile the Project
Open a terminal in the project directory and run:
```powershell
javac -cp "lib/ojdbc8.jar;src" -d bin src/model/*.java src/database/*.java src/*.java
```

### Step 2: Test Database Connection (Optional diagnostic)
```powershell
java -cp "bin;lib/ojdbc8.jar" DatabaseTest
```

### Step 3: Run the Web Server
```powershell
java -cp "bin;lib/ojdbc8.jar" WebServer
```

Access the interfaces in any web browser:
- **CampusDrop Web Dashboard**: [http://localhost:3000](http://localhost:3000)
- **OOP Architecture Reference**: [http://localhost:3000/oop](http://localhost:3000/oop)

### Step 4: Run Console Interface (Alternative)
```powershell
java -cp "bin;lib/ojdbc8.jar" Main
```
