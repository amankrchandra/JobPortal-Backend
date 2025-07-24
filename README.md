# Job Portal Backend (But it is not suitable and working for zidio-frontend part, for this(zidio-frontend) gonna be upload new backend.)

This is the backend service for the **Zidio Job Portal**, built using **Spring Boot**, **Spring Security**, **JWT**, and **MySQL**.

## 🚀 Features

- User registration & login (Student, Recruiter, Admin)
- JWT-based authentication
- Role-based authorization
- Job creation and application
- Resume upload support
- RESTful APIs for integration with frontend

## 🛠️ Tech Stack

- Java 17
- Spring Boot
- Spring Security + JWT
- Hibernate (JPA)
- MySQL
- Maven

## ⚙️ How to Run

Make sure MySQL is running and update `src/main/resources/application.properties` with your DB credentials.

Then:

```bash
./mvnw spring-boot:run

