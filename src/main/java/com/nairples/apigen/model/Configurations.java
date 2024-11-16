package com.nairples.apigen.model;

import lombok.Data;

@Data
public class Configurations {
    private boolean apiGateway;
    private boolean namingServer;
    private boolean keycloak;
    private boolean springSecurity;
    private boolean springCloudConfig;
    private boolean zipkin;
    private boolean actuator;
    private boolean h2Database;
}
