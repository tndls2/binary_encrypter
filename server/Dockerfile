FROM adoptopenjdk/openjdk11

ENV APP_HOME=/usr/app
WORKDIR ${APP_HOME}
COPY build/libs/binary_encrypter_server-0.0.1-SNAPSHOT-plain.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
