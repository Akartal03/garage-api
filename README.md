# garage-api

 Introduction
------------

An automated ticketing system that allows  customers to use your garage without human intervention.
When a car enters garage, it gives a unique ticket issued to the driver. 
The ticket issuing process includes us documenting the plate and the colour of the car and allocating an available slots
to the car before actually handing over a ticket to the driver.
When a vehicle holds number of slots with its own width, it leaves 1 unit slot to next one.
When a vehicle holds number of slots with its own width, it
leaves 1 unit slot to next one.
The customer will be allocated slot(s) which is nearest to the entry. 
At the exit the customer returns the ticket which then marks slot(s) they were using as being available.

Requirements
------------

For building and running the application you need:

- Java 11 
- Maven
- Redis



## Steps to Setup

**0.First need to setup Redis**

```bash
docker run --name redis -p 6379:6379 -d redis
```

**1. Clone the application**

```bash
https://github.com/Akartal03/garage-api.git
```

**2. Build and run the app using maven**

```bash
mvn package
java -jar target/garage-api-0.0.1-SNAPSHOT.jar

```

Alternatively, you can run the app without packaging it using

```bash
mvn spring-boot:run
```

The app will start running at <http://localhost:8080>.

You can also see all interfaces at swagger page <http://localhost:8080/swagger-ui.html#/>. 

## Explore Rest APIs

The app defines following CRUD APIs.

     /api/v1/garage/vehicle


## Rest API Examples

### Park Api 
Note1: 'type' can only 'car', 'truck' and 'jeep'

Note2: 'plate' has a format like '12-asd-13' and not case sensitive.

    POST URL: http://localhost:8080/api/v1/garage/vehicle/park
    BODY:  
    {
        "plate":"16-jgr-49",
        "color":"pink",
        "type":"car"
    }

### Leave Api

    POST URL: http://localhost:8080/api/v1/garage/vehicle/leave
    BODY:  
    {
        "ticketNumber": "e9321ba5-9ce0-49e6-8a50-0fb65ca513f8"
    }

### Status Api

    GET URL: http://localhost:8080/api/v1/garage/vehicle/status
