# PDF Auth App

A simple Spring Boot application that receives a PDF via HTML form and validates it using Apache PDFBox.

## Features
- HTML form to upload a PDF
- Validation checks:
  - File extension is .pdf
  - PDF can be parsed
  - Not encrypted
  - Has at least one page
- Clean separation of concerns (HTML, CSS, JS)

## Requirements
- Java 17+
- Maven 3.9+

## Run
```bash
mvn spring-boot:run
```
Then open http://localhost:8080/
