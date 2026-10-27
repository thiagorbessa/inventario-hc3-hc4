# Estágio 1: Compila a aplicação usando o Maven nativo
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Copia os arquivos do projeto para o container
COPY . .

# Compila o projeto gerando o .jar
RUN mvn clean package -DskipTests

# Estágio 2: Imagem leve de execução em Java 17
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia o .jar gerado no Estágio 1
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
