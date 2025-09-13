# syntax=docker/dockerfile:1.6
FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml .
COPY gateway-service/pom.xml     gateway-service/pom.xml
COPY auth-service/pom.xml        auth-service/pom.xml
COPY iam-service/pom.xml         iam-service/pom.xml
COPY profile-service/pom.xml     profile-service/pom.xml
COPY discovery-service/pom.xml   discovery-service/pom.xml
COPY common-web/pom.xml          common-web/pom.xml
COPY catalog-service/pom.xml     catalog-service/pom.xml
COPY inventory-service/pom.xml   inventory-service/pom.xml

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests -pl discovery-service -am dependency:go-offline

COPY common-web/src            common-web/src
COPY discovery-service/src     discovery-service/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests -pl discovery-service -am -Dspring-boot.repackage.skip=true install \
 && mvn -B -DskipTests -pl discovery-service -Dspring-boot.repackage.skip=false package spring-boot:repackage \
 && mkdir -p /out \
 && JAR_PATH=$(find discovery-service/target -maxdepth 1 -type f -name "*-SNAPSHOT.jar" ! -name "original-*" -print -quit) \
 && cp "$JAR_PATH" /out/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=builder /out/app.jar /app/app.jar
EXPOSE 8761
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
