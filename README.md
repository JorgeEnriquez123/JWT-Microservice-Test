# Spring Microservice + JWT Test implementation
This project demonstrates a basic implementation of JWT Security in a simple Microservice architecture.

The process consists in sending a JWT on each request, which will be caught by the API Gateway and validate it through a User-Service.
<br>
If validation goes wrong, the request will end with an Authorization Error. otherwise, the User-service will return a UserDto which contains the username and roles.
<br>
This info will be passed on to the inner microservices through headers to make use of Role-based secured endpoints if needed.

![Flow Diagram](/images/Flow%20Diagram.jpg "Flow diagram")

### Features:
* API Gateway implementation
* Config Server implementation
* Initial Users and Roles Loaded. (USER + ADMIN)
* Role-Based Access Control on inner microservice.
* JWT Exception Handling.
* Authentication Error Handling.
* Authorization Error Handling.

### Technologies
* **[Hibernate](https://hibernate.org)** as ORM for database interactions.
* **[JPA](https://en.wikipedia.org/wiki/Jakarta_Persistence)** for accessing and persisting data based on Java Objects.
* **[MySQL](https://www.mysql.com/)** database to record User Data.
* **[Lombok](https://projectlombok.org/features)** to reduce boilerplate code.
* **[Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)** for routing paths.
* **[Spring Cloud Config](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/)** for concentrate configuration.
* **[Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/reference/html/)** for service discovery and registration in Eureka.
* **[WebClient](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html)** for performing asynchronous HTTP requests.

## Usage
### API Endpoints
* **GET** `/user`: returns all users in database.
* **POST** `/user`: saves a user.
* **POST** `/user/login`: Authenticates a user and returns a JWT token.
* **POST** `/user/validate`: Validates JWT and returns UserDto.
<br><br>
* **GET** `/bear`: returns all bears in database | **Requires being Authenticated.**
* **POST** `/bear`: saves a bear | **Requires ```ADMIN``` role.**

### Login Process
1. User makes a POST request to `/user/login` with a [LoginRequest](https://github.com/JorgeEnriquez123/JWT-Microservice-Test/blob/main/user-service/src/main/java/com/jorge/userservice/model/dto/LoginRequest.java) Object as body.
```json
{
    "username": "Jorge",
    "password": "jorge123"
}
```
2. Server returns a [LoginResponse](https://github.com/JorgeEnriquez123/JWT-Microservice-Test/blob/main/user-service/src/main/java/com/jorge/userservice/model/dto/LoginResponse.java) Object that contains the Access Token.
```json
{
"access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKb3JnZSIsImlhdCI6MTcwNzA2OTg3MCwiZXhwIjoxNzA3MDY5OTMwfQ.GBlPmNFZvekUkqjZUaNhDQeiLG40lwAvs8nfP6yXB9w"
}
```
User needs to include the Access Token in all their requests to access the microservices.

### Gateway Filter Process
1. Gateway checks if incoming request route requires authentication.
2. If path requires authentication, it makes an asynchronous HTTP request to ```user-service``` along with the JWT for verification.
3. If verification goes right, it returns a [UserDTO](https://github.com/JorgeEnriquez123/JWT-Microservice-Test/blob/main/gateway/src/main/java/com/jorge/gateway/dto/UserDto.java) with info about the user (username and roles)
```json
{
  "username": "Jorge",
  "roles": [
    "USER"
  ]
}
```
4. Gateway mutates the former request adding retrieved User info to the request as header.
5. Inner microservices gets this info through a filter and set their SecurityContextHolder for RBAC controllers.