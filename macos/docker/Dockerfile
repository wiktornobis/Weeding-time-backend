FROM openjdk:21-jdk

# Ustaw autora obrazu (to pole informacyjne, opcjonalne)
LABEL maintainer="baeldung.com"

# Skopiuj plik JAR z lokalnego systemu do kontenera
COPY target/*.jar app.jar

# Użyj ENTRYPOINT do uruchomienia aplikacji
ENTRYPOINT ["java", "-jar", "/app.jar"]