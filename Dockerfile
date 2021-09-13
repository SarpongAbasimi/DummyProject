FROM openjdk:8-jre-alpine

WORKDIR /dummyproject

COPY . .

EXPOSE 5000

ADD target/scala-**/dummyProject-assembly-0.1.jar .

ENTRYPOINT ["java","-jar","dummyProject-assembly-0.1.jar"]