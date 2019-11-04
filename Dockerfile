FROM openjdk:8-jdk-alpine
MAINTAINER Yahya

VOLUME /tmp
COPY target/reactive-spring-twitter*.jar reactive-spring-twitter.jar
CMD ["java","-jar","reactive-spring-twitter.jar"]
