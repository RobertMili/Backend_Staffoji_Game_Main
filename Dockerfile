FROM openjdk:17-oracle
VOLUME /tmp
ARG DEPENDENCY=target/dependency
ARG BOOSTAPPDBUSER='user'
ARG BOOSTAPPDBSECRET='password'
ENV DATASOURCE=${DATASOURCE}
ENV USER=${USER}
ENV PASSWORD=${PASSWORD}
ENV EMAIL_PASSWORD =${EMAIL_PASSWORD}
#this is saying that the profile is dev
ENV SPRING_PROFILES_ACTIVE=dev
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.backend_staffoji_game.BackendStaffojiGameApplication"]
