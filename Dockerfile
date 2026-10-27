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