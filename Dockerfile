# Etapa 1: compila e empacota a aplicacao com Maven e Java 17.
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia primeiro o pom para aproveitar cache das dependencias.
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

# Depois copia o codigo-fonte e gera o jar da aplicacao.
COPY src ./src
RUN mvn -B -DskipTests clean package

# Etapa 2: cria uma imagem final menor, so com Java para executar o jar.
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Cria um usuario sem privilegios para rodar a aplicacao com mais seguranca.
RUN addgroup --system spring && adduser --system --ingroup spring spring

# Copia o jar gerado na etapa de build para a imagem final.
COPY --from=build /app/target/*.jar /app/app.jar

USER spring

# Documenta a porta usada pela aplicacao dentro do container.
EXPOSE 8081

# Comando que inicia a aplicacao Spring Boot.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
