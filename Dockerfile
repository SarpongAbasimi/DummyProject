FROM openjdk:8-jre-alpine

WORKDIR /dummyproject

COPY . .

ADD target/scala-**/dummyproject_2.13-0.1.jar .

ENTRYPOINT ["java","-jar","/dummyproject_2.13-0.1.jar"]