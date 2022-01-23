FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine
LABEL org.opencontainers.image.authors="azharrnaeem@gmail.com"
EXPOSE 8080

COPY impactanalysis.jar impactanalysis.jar

ENTRYPOINT  ["java", "-jar", "/impactanalysis.jar"]
