package com.nairples.apigen.model;

import java.util.List;

import lombok.Data;

@Data
public class MavenConfiguration {
    private String groupId;
    private String artifactId;
    private String version;
    private String packaging;
    private String javaVersion;
    private String springBootVersion;
    private String description;
    private String url;

    private List<Dependency> dependencies;
    private List<Plugin> plugins;


    @Data
    public static class Dependency {
        private String groupId;
        private String artifactId;
        private String version;
        private String scope; // e.g., compile, test, provided, etc.
    }

    @Data
    public static class Plugin {
        private String groupId;
        private String artifactId;
        private String version;
        private List<PluginExecution> executions;

        @Data
        public static class PluginExecution {
            private String id;
            private List<String> goals;
        }
    }
}

