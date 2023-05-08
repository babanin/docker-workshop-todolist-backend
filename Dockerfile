FROM maven:3.6.3-openjdk-11-slim AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:11.0.19_7-jre-jammy

ENV USER_ID=65535
ENV GROUP_ID=65535
ENV USER_NAME=javauser
ENV GROUP_NAME=javauser

WORKDIR /opt/app

RUN groupadd -g $GROUP_ID $GROUP_NAME && useradd -u $USER_ID -g $GROUP_NAME -s /bin/sh $USER_NAME

COPY --from=builder /build/target/to-do-list-0.0.1-SNAPSHOT.jar app.jar
RUN chown -R $USER_NAME:$USER_NAME .
USER $USER_NAME
ENTRYPOINT ["java", "-jar", "app.jar"]
