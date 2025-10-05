FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
RUN mkdir uploads


ENV DB_URL=localhost:5432
ENV FILE_URL=/uploads
ENV FILE_PATH=/uploads

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 3000