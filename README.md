# Job Portal Backend

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
📁 Project Structure
css
Copy
Edit
src/
 └── main/
     ├── java/com/zidio/jobportal/
     │   ├── controller/
     │   ├── model/
     │   ├── repository/
     │   ├── security/
     │   └── ...
     └── resources/
         └── application.properties
🙏 Acknowledgements
Special thanks to:

Spring Boot & Spring Security teams

OpenAI ChatGPT for backend guidance

✨ Author
Aman Kumar Chandra

LinkedIn • GitHub
