# Use an official Java 8 runtime as the parent image
FROM openjdk:8-jdk-slim

# Install Docker CLI, Maven, and Git
RUN apt-get update && \
    apt-get install -y curl maven git && \
    curl -fsSL https://get.docker.com -o get-docker.sh && \
    sh get-docker.sh && \
    rm -rf /var/lib/apt/lists/* /get-docker.sh

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project into the container
COPY ./pom.xml ./pom.xml
COPY ./src ./src

# Build the application (without running tests)
RUN mvn clean install -DskipTests
# Checkout the compatible version of JPF for Java 8
# This assumes that 'jpf-core-tag-compatible-with-java-8' is a placeholder for an actual tag or commit hash.
# You will need to replace it with the actual tag or commit hash that is compatible with Java 8.
RUN git clone https://github.com/javapathfinder/jpf-core.git /jpf && \
    cd /jpf && \
    git checkout java-8 && \
    ./gradlew buildJars

# Set environment variables for Java Pathfinder
ENV JPF_HOME /jpf/jpf-core
ENV PATH $PATH:$JPF_HOME/bin

# Expose port 8080 for Spring Boot
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "target/feedbackloop-0.0.1-SNAPSHOT.jar"]
