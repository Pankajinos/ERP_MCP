# MANIT Student ERP MCP Server ??

A production-ready **Model Context Protocol (MCP) Server** built with **Java 21**, **Spring Boot 3.3+**, and **Spring AI MCP Server**. This server connects to university ERP endpoints (`erpapi.manit.ac.in`) and exposes granular student academic, fee, and faculty details as focused AI Tools for **Claude Desktop** and **GitHub Copilot Chat**.

---

## ??? Architecture Overview

The MCP server follows Clean Architecture principles, ensuring strict separation of concerns, immutability via Java 21 Records, and focused data delivery to LLMs.

```
                      [ Claude Desktop / GitHub Copilot Chat ]
                                         ¦
                                   (STDIO / SSE)
                                         ¦
                                         ?
                            +-------------------------+
                            ¦      AcademicTools      ¦  <-- MCP Tools Layer (@Tool)
                            +-------------------------+
                                         ¦
                        +---------------------------------+
                        ?                                 ?
              +------------------+               +------------------+
              ¦ AcademicService  ¦               ¦    FeeService    ¦  <-- Service Layer
              +------------------+               +------------------+
                       ¦                                  ¦
                       +----------------------------------+
                                        ¦
                                        ?
                             +---------------------+
                             ¦    ErpDataMapper    ¦  <-- DTO Transformation
                             +---------------------+
                                        ¦
             +--------------------------+--------------------------+
             ?                          ?                          ?
   +-------------------+      +-------------------+      +-------------------+
   ¦  ResultApiClient  ¦      ¦RegistrationClient ¦      ¦   FeeApiClient    ¦ <-- WebClient
   +-------------------+      +-------------------+      +-------------------+
             ¦                          ¦                          ¦
             +--------------------------+--------------------------+
                                        ¦ (HTTP GET with JSON Payload)
                                        ?
                          [ erpapi.manit.ac.in REST APIs ]
```

---

## ?? Detailed Tool Summary & Technical Specifications

The server exposes 4 granular, high-value MCP tools:

---

### 1?? `getFeeDetailsPerSemester(int semester)`

#### Description & Purpose
Returns complete fee details and line-item breakdown for a specific semester, including semester number, total fee amount, and individual fee heads.

#### Input Parameters
| Parameter Name | Data Type | Mandatory / Optional | Description |
|---|---|---|---|
| `semester` | `int` | **Mandatory** | Semester term number (e.g., `1`, `2`, `5`, `8`). |

#### Output Example
```json
{
  "semester": 5,
  "totalAmount": 50000.0,
  "itemCount": 3,
  "items": [
    {
      "feeHead": "Tuition Fee",
      "amount": 42000.0,
      "session": "2024-2025",
      "semesterDesc": "Sem 5"
    },
    {
      "feeHead": "Hostel Rent",
      "amount": 7500.0,
      "session": "2024-2025",
      "semesterDesc": "Sem 5"
    },
    {
      "feeHead": "SF-Library Fee",
      "amount": 500.0,
      "session": "2024-2025",
      "semesterDesc": "Sem 5"
    }
  ]
}
```

---

### 2?? `getFeeDetailPerItem(String item, Integer semester, BigDecimal minAmount, BigDecimal maxAmount)`

#### Description & Purpose
Retrieves student fee details using optional filters. All parameters are optional and multiple filters can be combined. If no filters are provided, returns all available fee details.

#### Input Parameters
| Parameter Name | Data Type | Mandatory / Optional | Description |
|---|---|---|---|
| `item` | `String` | **Optional** | Fee item name or keyword (e.g. `Tuition Fee`, `Hostel Rent`, `SF-Library Fee`, `Bus Fees`, etc.). |
| `semester` | `Integer` | **Optional** | Semester number between `0` and `10`. |
| `minAmount` | `BigDecimal` | **Optional** | Minimum fee amount threshold (returns items with amount >= `minAmount`). |
| `maxAmount` | `BigDecimal` | **Optional** | Maximum fee amount threshold (returns items with amount <= `maxAmount`). |

#### Output Example
```json
{
  "totalAmount": 7500.00,
  "matchCount": 1,
  "feeItems": [
    {
      "feeHead": "Hostel Rent",
      "amount": 7500.0,
      "semester": 5,
      "semesterDesc": "Sem 5",
      "session": "2024-2025"
    }
  ]
}
```

---

### 3?? `getSubjectMarksPerSubject(String subject)`

#### Description & Purpose
Returns detailed examination marks, score breakdown (midterm, endterm, total obtained, max marks), letter grade, grade points, and credits for a specific subject by subject code or subject name.

#### Input Parameters
| Parameter Name | Data Type | Mandatory / Optional | Description |
|---|---|---|---|
| `subject` | `String` | **Mandatory** | Subject course code (e.g. `MDS316`, `MDS323`) or course title (e.g. `Data Mining`). |

#### Output Example
```json
{
  "subjectCode": "MDS323",
  "subjectName": "Data Mining",
  "semester": 5,
  "midTermMarks": 39.0,
  "endTermMarks": 39.0,
  "marksObtained": 78.0,
  "totalMarks": 100.0,
  "grade": "A",
  "gradePoint": "8.0",
  "credit": 3.0
}
```

---

### 4?? `getSubjectFacultyPerSubject(String subject)`

#### Description & Purpose
Returns assigned faculty instructor and course details for a specific subject by subject code or subject name.

#### Input Parameters
| Parameter Name | Data Type | Mandatory / Optional | Description |
|---|---|---|---|
| `subject` | `String` | **Mandatory** | Subject course code (e.g. `MDS316`, `MDS323`) or course title (e.g. `Data Mining`). |

#### Output Example
```json
{
  "subjectCode": "MDS323",
  "subjectName": "Data Mining",
  "facultyName": "Dr. Ali Ahmed",
  "semester": 5,
  "department": "Computer Science & Engineering"
}
```

---

## ?? How to Build & Run Locally

### Prerequisites
- **Java 21+** (`java -version`)
- **Apache Maven 3.9+** (`mvn -version`)

### Build Command
```bash
mvn clean package -DskipTests=false
```

### Run Executable JAR
```bash
java -jar target/student-erp-mcp-server-1.0.0-SNAPSHOT.jar
```

---

## ?? Sample Prompts for LLM Testing

1. *"Show my fee details for semester 5."*
2. *"How much is my Hostel Rent?"*
3. *"Show my library fee for semester 5."*
4. *"Show fees between 1000 and 5000."*
5. *"What marks did I get in Data Mining (MDS323)?"*
6. *"Who is teaching MDS323?"*
