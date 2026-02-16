FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN rm -f src/main/resources/keystore.p12

RUN mkdir -p src/main/resources
RUN keytool -genkeypair -alias cardmanager \
    -keyalg RSA -keysize 2048 \
    -storetype PKCS12 \
    -keystore src/main/resources/keystore.p12 \
    -validity 3650 \
    -storepass 123456 \
    -dname "CN=CardManager, OU=Dev, O=Company, L=SaoLuis, ST=MA, C=BR"

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]