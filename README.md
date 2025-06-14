# Credit Management App

This is Loan Management backend application. It includes business logics like creating customer, creating admin user, creating loans and paying installments.

## Database

H2 has been used, this endpoint can be used to reach it. http://localhost:8080/h2-console

Username: sa
Password: (leave empty)

## Requirements

- Java 21
- Maven
- Docker

## Swagger

http://localhost:8080/swagger-ui/index.html#/

## User Roles
* ADMIN
* CUSTOMER

## Endpoints

Most of endpoints are secured so before requesting the jwt token should be taken from acces-token endpoint.

The role of user is important, the CUSTOMER role has restricted access to endpoints

There are predefined created admin user and customers.
```
role:       ADMIN
username:   oktay1
password:   123123

role:       CUSTOMER
username:   customer1
password:   pass1

role:       CUSTOMER
username:   customer2
password:   pass1

role:       CUSTOMER
username:   customer3
password:   pass1
```

---

```
POST /api/v1/users                          Create Admin User
POST /api/v1/access-token                   Get Access Token to Access Secured Endpoints

POST /api/v1/loans                          Create Loan For Customer
POST /api/v1/loans/:loanId/payment          Pay Installments of Loan
GET  /api/v1/customers/:customerId/loans    List loans of customer

GET  /api/v1/customers                      List customers
POST /api/v1/customers                      Create Customer

GET  /api/v1/loans/:loanId/installments     List installments of Loan 
 
```

## Test
```
./mvnw test
```

## Application Configurations
The configurations can be changed from application.properties file before running application.

```
//pre-defined installment counts
loan.allowed-installment-counts=3,6,9,12
//minimum interest rate
loan.allowed-minimum-interest-rate=0.1
//maximum interest rate
loan.allowed-maximum-interest-rate=0.5
//early payment reward rate
loan.payment-reward-rate=0.001
//late payment penalty rate
loan.payment-penalty-rate=0.001
```

## Run Application

From Terminal
```
./mvnw spring-boot:run
```

Run with Docker
```
./mvnw package
docker-compose up --build
```

## Usage

## 🔐 Authentication
Use `/api/v1/access-token` to obtain a JWT token.

for ADMIN user
```
curl -X POST http://localhost:8080/api/v1/access-token \
  -H "Content-Type: application/json" \
  -d '{"username": "oktay1", "password": "123123"}'
```

for CUSTOMER user
```
curl -X POST http://localhost:8080/api/v1/access-token \
  -H "Content-Type: application/json" \
  -d '{"username": "customer1", "password": "pass1"}'
```

## Postman

[Link](https://www.postman.com/dark-equinox-303092/workspace/ing-hub/collection/1311361-1570a655-7463-448c-8bfd-dbd1064ea4f2?action=share&creator=1311361&active-environment=1311361-126b1a42-c859-4c4e-a075-4846202eca99)

