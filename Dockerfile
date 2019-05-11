FROM gradle:5.4.1-jdk11 AS java_build

USER root

WORKDIR /lambda-server-java

COPY lambda-server-java .

RUN ./gradlew --stacktrace build 

FROM golang:1.12.5-alpine3.9 AS go_build

WORKDIR /lambda-starter

ADD lambda-starter .

RUN go build cmd/starterd/starterd.go


FROM openjdk:12.0.1-jdk

COPY --from=java_build /lambda-server-java/build/dependencieslib/*.jar /lambda-server/
COPY --from=java_build /lambda-server-java/build/libs/*.jar /lambda-server/
COPY --from=go_build /lambda-starter/starterd /bin/starterd

CMD [ starterd ]