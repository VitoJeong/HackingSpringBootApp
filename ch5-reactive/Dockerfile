#openJDK 제공자가 제공한 JDK 기반 컨테이너  생성
#FROM adoptopenjdk/openjdk8:latest
#도커파일 내부인자 지정
#ARG JAR_FILE=target/*.jar
#JAR파일을 복사해서 컨테이너 내부에 'app.jar'로 붙여넣는다.
#COPY ${JAR_FILE} app.jar
#컨테이너 내부에서 java -jar /app.jar 명령을 수행한다.
#ENTRYPOINT ["java", "-jar","/app.jar"]

# 계층화 적용
#빌더로 사용할 컨테이너 생성
FROM adoptopenjdk/openjdk8:latest as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
#extract 명령으로 레이어 추출
RUN java -Djarmode=layertools -jar application.jar extract

#두번째 컨테이너 생성
FROM adoptopenjdk/openjdk8:latest
WORKDIR application
#빌더 컨테이너에서 여러 레이어를 복사 - 캐시알고리즘을 적용해서 서드파티 라이브러리는 캐시될 수 있다.
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
#스프링부트 커스텀 런처로 애플리케이션을 실행(불필요한 JAR 파일 압축해제를 생략)
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
