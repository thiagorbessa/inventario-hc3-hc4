# Usa uma imagem oficial do Java 17 leve (Alpine)
FROM eclipse-temurin:17-jdk-alpine

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo .jar compilado para dentro do container
COPY target/*.jar app.jar

# Expõe a porta 8080 do sistema
EXPOSE 8080

# Executa a aplicação Java
ENTRYPOINT ["java", "-jar", "app.jar"]

# Estágio 1: Compila a aplicação Java
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY . .
RUN ./mvnw clean package -DskipTests

# Estágio 2: Cria a imagem leve final de execução
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
