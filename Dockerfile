FROM openjdk:17-oracle
VOLUME /tmp
ARG DEPENDENCY=target/dependency
ARG BOOSTAPPDBUSER='user'
ARG BOOSTAPPDBSECRET='password'
ENV DATASOURCE=jdbc:sqlserver://boostappdb20212.database.windows.net:1433;database=StepDB;user=Boostapp@boostappdb20212;password={your_password_here};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
ENV BOOSTAPPDBUSER=Boostapp
ENV BOOSTAPPDBSECRET=QwertyBoost123
ENV APP_PASSWORD = cqra blea houy xgex
#this is saying that the profile is dev
ENV SPRING_PROFILES_ACTIVE=dev
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.backend_staffoji_game.BackendStaffojiGameApplication"]
