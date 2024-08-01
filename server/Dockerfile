FROM adoptopenjdk/openjdk11
ENV APP_HOME=/usr/app
WORKDIR ${APP_HOME}
COPY build/libs/application.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]