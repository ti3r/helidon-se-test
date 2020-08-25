#### Helidon test application

Simple application to demonstrate helidon framework capabilities using maven and kotlin.    

##### Run locally
Execute command
```bash
./mvnw clean compile exec:java
```

Once application is running visit health probe [http://localhost:8080/health](http://localhost:8080/health)


##### Demonstrate Kafka Capabilities

Once the server is running you can visit the following link in order to connect to the test kafka cluster
and send a test message. [http://localhost:8080/chat?msg=mesage_here](http://localhost:8080/chat?msg=mesage_here)

