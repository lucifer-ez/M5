FROM maven:3.9.6-eclipse-temurin-21

RUN apt-get update && apt-get install -y wget gnupg ca-certificates curl unzip \
    --no-install-recommends \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub \
       | apt-key add - \
    && echo "deb [arch=amd64] https://dl.google.com/linux/chrome/deb/ stable main" \
       >> /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve -q
COPY . .
RUN mvn clean install -DskipTests -q
CMD ["mvn", "test"]
