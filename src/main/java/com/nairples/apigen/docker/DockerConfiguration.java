package com.nairples.apigen.docker;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DockerConfiguration {
    private String baseImage;
    private String baseImageVersion;
    private String jarFile;
    private String appPort;
    private String dbPassword;
    private String networkName;
    private List<String> environmentVariables;
    private String  artifactId;
    private Object databaseUrl;
    private Map<String, Map<String, String>> additionalServices = new HashMap<>();

}
