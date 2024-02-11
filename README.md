# digg-demo

## Running the application in dev mode
To make Quarkus setup postgres container locally, you can run in dev-mode.
Provided that you have docker installed and running.

Run in dev mode
```shell
./mvnw compile quarkus:dev
```

## Packaging and running the application

### Create and run docker container
> **Requires Postgres!**

To build and create docker image, run
```shell 
./mvnw install -Dquarkus.container-image.build=true
```
You can then verify that the image was created with (under name <username>/digg-demo)
```shell
docker images
```
To run the image in a container, run
```shell
docker run --rm -p 8080:8080 -e PG_IP=<postgres ip> -e PG_PASS=<postgres password> -e PG_USER=<postgres user> quarkus/digg-demo-jvm
```

### Create and run JAR

```shell
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.

Run the jar with
```shell
java -jar target/quarkus-app/quarkus-run.jar
```