# MANIT Student ERP MCP Server 🚀

A production-ready **Model Context Protocol (MCP) Server** built with **Java 21**, **Spring Boot 3.3+**, and **Spring AI MCP Server**. This server securely connects to university ERP endpoints (`erpapi.manit.ac.in`) and exposes student academic, registration, and fee details as high-value AI Tools for **Claude Desktop** and **GitHub Copilot Chat**.

---

## 🏛️ Architecture Overview

The MCP server follows Clean Architecture principles, ensuring strict separation of concerns, immutability via Java 21 Records, and zero raw ERP data leakage to the LLM.

```
                      [ Claude Desktop / GitHub Copilot Chat ]
                                         │
                                   (STDIO / SSE)
                                         │
                                         ▼
                            ┌─────────────────────────┐
                            │      AcademicTools      │  <-- MCP Tools Layer (@Tool)
                            └────────────┬────────────┘
                                         │
                        ┌────────────────┴────────────────┐
                        ▼                                 ▼
              ┌──────────────────┐               ┌──────────────────┐
              │ AcademicService  │               │    FeeService    │  <-- Service Layer
              └────────┬─────────┘               └────────┬─────────┘
                       │                                  │
                       └────────────────┬─────────────────┘
                                        │
                                        ▼
                             ┌─────────────────────┐
                             │    ErpDataMapper    │  <-- DTO Transformation
                             └──────────┬──────────┘
                                        │
             ┌──────────────────────────┼──────────────────────────┐
             ▼                          ▼                          ▼
   ┌───────────────────┐      ┌───────────────────┐      ┌───────────────────┐
   │  ResultApiClient  │      │RegistrationClient │      │   FeeApiClient    │ <-- WebClient
   └─────────┬─────────┘      └─────────┬─────────┘      └─────────┬─────────┘
             │                          │                          │
             └──────────────────────────┼──────────────────────────┘
                                        │ (HTTP GET with JSON Payload)
                                        ▼
                          [ erpapi.manit.ac.in REST APIs ]
```

### Key Architectural Layers

1. **MCP Tool Layer (`tools`)**: Exposes domain capabilities (`getAcademicSummary`, `getSemesterDetails`, `getSubjectDetails`, `getRegistrationInfo`, `getFeeInfo`, `searchAcademicRecords`, `getStudentDashboard`) with rich metadata for LLM tool selection.
2. **Service Layer (`services`)**: Handles data aggregation, fee categorization, weighted CGPA calculations, and multi-endpoint data composition using Project Reactor (`Mono.zip`).
3. **Mapper & DTO Layer (`dto`, `mapper`)**: Transforms verbose raw ERP responses into lightweight domain records to save LLM context tokens.
4. **ERP WebClient Layer (`clients`)**: Manages HTTP GET calls with JSON bodies, header injection, exponential backoff retries, timeouts, and logging.
5. **Configuration Layer (`config`)**: Externalized configuration properties (`ErpProperties`) and WebClient bean initialization.

---

## 🛠️ MCP Tools Reference

| Tool Name | Parameters | Description |
|---|---|---|
| `getAcademicSummary` | None | Returns overall CGPA, enrolled program, student full name, and semester breakdown. |
| `getSemesterDetails` | `semester` (int) | Merges results, subject faculty assignments, and fee breakdowns for a specific semester. |
| `getSubjectDetails` | `subjectCode` (String) | Returns title, faculty, midterm, endterm, total marks, grade, and credits for a subject code. |
| `getRegistrationInfo` | `semester` (Integer, optional) | Returns registered subjects, credit count, assigned faculty, and feedback submission status. |
| `getFeeInfo` | `semester` (Integer), `year` (Integer) | Returns itemized fee breakdown grouped into Academic Fee, Hostel Fee, and Other Fee. |
| `searchAcademicRecords`| `query` (String) | Filters academic records by grade (e.g. "A grade"), labs, subject title, or credit count. |
| `getStudentDashboard` | None | Executive dashboard summarizing CGPA, active semester, earned credits, backlogs, and pending fees. |

---

## ⚙️ Configuration (`application.yml`)

The application allows externalized configuration via `application.yml` or environment variables:

```yaml
erp:
  api:
    base-url: https://erpapi.manit.ac.in/api
    result-path: /student_result
    registration-path: /fetch_register
    fee-path: /student_fees
    auth-token: ${ERP_AUTH_TOKEN:mock-bearer-token}
    default-student-uid: ${STUDENT_UID:4705}
    default-program-id: ${PROGRAM_ID:82}
  client:
    connect-timeout-ms: 5000
    read-timeout-ms: 10000
    max-retry-attempts: 3
    backoff-period-ms: 1000
```

---

## 🚀 How to Build & Run Locally

### Prerequisites
- **Java 21** or higher (`java --version`)
- **Apache Maven 3.9+** (`mvn --version`)

### 1. Build the Executable JAR
```bash
mvn clean package -DskipTests=false
```

### 2. Run the MCP Server via Command Line
```bash
java -jar target/student-erp-mcp-server-1.0.0-SNAPSHOT.jar
```

---

## 🔌 Connecting to Claude Desktop

Add the server definition to your `claude_desktop_config.json` file:

- **Windows**: `%APPDATA%\Claude\claude_desktop_config.json`
- **macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`

```json
{
  "mcpServers": {
    "student-erp-mcp": {
      "command": "java",
      "args": [
        "-jar",
        "C:/Users/rajar/.gemini/antigravity/scratch/student-erp-mcp-server/target/student-erp-mcp-server-1.0.0-SNAPSHOT.jar"
      ],
      "env": {
        "ERP_AUTH_TOKEN": "your-actual-bearer-token-here",
        "STUDENT_UID": "4705",
        "PROGRAM_ID": "82"
      }
    }
  }
}
```

Restart Claude Desktop. You will see a hammer icon 🔨 indicating that `student-erp-mcp` tools are active!

---

## 🔌 Connecting to GitHub Copilot Chat (VS Code)

In your VS Code `settings.json` or `.vscode/mcp.json`:

```json
{
  "github.copilot.mcpServers": {
    "student-erp-mcp": {
      "command": "java",
      "args": [
        "-jar",
        "C:/Users/rajar/.gemini/antigravity/scratch/student-erp-mcp-server/target/student-erp-mcp-server-1.0.0-SNAPSHOT.jar"
      ],
      "env": {
        "ERP_AUTH_TOKEN": "your-actual-bearer-token-here"
      }
    }
  }
}
```

---

## 💬 Sample Prompts for LLM Testing

Try these prompts in Claude Desktop or GitHub Copilot Chat:

1. **Academic Summary**:
   > *"What is my overall CGPA and how did I perform in each semester?"*

2. **Semester Details**:
   > *"Give me full details for Semester 5 including my subjects, faculty names, and fees."*

3. **Subject Search**:
   > *"What marks did I get in Data Mining (MDS323) and who taught it?"*

4. **Fee Breakdown**:
   > *"Break down my fees for semester 5 into academic, hostel, and miscellaneous."*

5. **Search Academic Records**:
   > *"Show me all subjects where I scored an A grade or took lab courses."*

6. **Student Dashboard**:
   > *"Show my executive student dashboard with my CGPA, total credits, backlogs, and fee dues."*

---

## 🖼️ Architecture & Execution Placeholders

| Feature | Visual Interface |
|---|---|
| **Claude Desktop Tool Discovery** | ![Claude Tools Placeholder](https://via.placeholder.com/600x300.png?text=Claude+Desktop+MCP+Tools+Connected) |
| **Tool Execution Response** | ![Tool Output Placeholder](https://via.placeholder.com/600x300.png?text=Minimal+Structured+JSON+Response) |

---

## 🛡️ License & Principles
Built under Clean Architecture guidelines using Spring AI MCP Server framework. Designed for minimal token usage, stateless operation, and complete protection of sensitive raw ERP data.

#### Some sample Queries
1. Fee Breakdown of this sem
2. What was my sgpa im sem 3
3. How much does i Scored in DBMS 
4. my subjects in sem 4
5. Who taught me machine learning