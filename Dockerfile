# ---- Build stage: compiles your app ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# copy Maven wrapper + pom first to cache dependencies
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -q -B -DskipTests dependency:go-offline

# now copy source and build
COPY src src
RUN ./mvnw -q -B -DskipTests package

# ---- Run stage: small runtime image ----
FROM eclipse-temurin:17-jre
WORKDIR /app
# If your JAR name isn’t a SNAPSHOT, change the pattern to *.jar
COPY --from=build /app/target/*-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

