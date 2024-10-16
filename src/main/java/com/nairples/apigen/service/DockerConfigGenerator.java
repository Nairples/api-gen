package com.nairples.apigen.service;

import com.nairples.apigen.docker.DockerConfiguration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class DockerConfigGenerator {

    public void generateDockerConfigs(DockerConfiguration request) {
        String dockerFileContent = buildDockerFile(request);
        String dockerComposeContent = buildDockerCompose(request);

        try {
            // Write Dockerfile
            Path dockerFilePath = Paths.get("src/main/docker/Dockerfile");
            Files.createDirectories(dockerFilePath.getParent());
            Files.write(dockerFilePath, dockerFileContent.getBytes());

            // Write docker-compose.yml
            Path dockerComposePath = Paths.get("src/main/docker/docker-compose.yml");
            Files.write(dockerComposePath, dockerComposeContent.getBytes());
        } catch (IOException e) {
            // TODO: Handle exceptions properly
        }
    }

    // Method to build the Dockerfile content
    private String buildDockerFile(DockerConfiguration request) {
        return """
                FROM %s:%s
                WORKDIR /app
                COPY . .
                RUN ./mvnw clean package -DskipTests
                EXPOSE %s
                ENTRYPOINT ["java", "-jar", "/app/target/%s.jar"]
                """.formatted(
                request.getBaseImage(),            // e.g., "openjdk"
                request.getBaseImageVersion(),     // e.g., "17-jdk-alpine"
                request.getAppPort(),              // e.g., "8080"
                request.getArtifactId()            // e.g., "my-app"
        );
    }

    // Method to build Docker Compose content with default app service and dynamic services
    private String buildDockerCompose(DockerConfiguration request) {
        // Default app service
        StringBuilder dockerComposeContent = new StringBuilder();
        dockerComposeContent.append("""
                version: "3.8"
                services:
                  app:
                    build:
                      context: .
                      dockerfile: src/main/docker/Dockerfile
                    ports:
                      - "%s:%s"
                    environment:
                      - DATABASE_URL=%s
                """.formatted(
                request.getAppPort(),                        // exposed app port
                request.getAppPort(),                        // mapped app port
                request.getDatabaseUrl()                     // database URL
        ));

        // Dynamically add more services based on request configuration
        for (Map.Entry<String, Map<String, String>> serviceEntry : request.getAdditionalServices().entrySet()) {
            String serviceName = serviceEntry.getKey();
            Map<String, String> serviceConfig = serviceEntry.getValue();

            dockerComposeContent.append("""
                  %s:
                    image: %s
                    ports:
                      - "%s:%s"
                    environment:
                      %s
                """.formatted(
                    serviceName,
                    serviceConfig.getOrDefault("image", "default-image"),
                    serviceConfig.getOrDefault("port", "1234"),
                    serviceConfig.getOrDefault("port", "1234"),
                    buildEnvironmentVariables(serviceConfig.get("environmentVariables"))
            ));
        }

        return dockerComposeContent.toString();
    }

    // Helper to format environment variables for Docker Compose
    private String buildEnvironmentVariables(String envVars) {
        if (envVars == null || envVars.isEmpty()) {
            return "";
        }

        StringBuilder envBuilder = new StringBuilder();
        String[] envVariables = envVars.split(",");
        for (String var : envVariables) {
            envBuilder.append(String.format("  - %s\n", var.trim()));
        }
        return envBuilder.toString();
    }
}

