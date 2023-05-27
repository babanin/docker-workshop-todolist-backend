FROM babanin/maven-with-deps

WORKDIR /build

COPY . .

RUN mvn package

FROM eclipse-temurin:20-jammy

WORKDIR /opt/app

COPY --from=0 /build/target/to-do-list-0.0.1-SNAPSHOT.jar app.jar

RUN groupadd javauser
RUN useradd -g javauser -s /bin/sh javauser
RUN chown -R javauser:javauser .

USER javauser

ENTRYPOINT ["java" , "-jar", "app.jar"]

