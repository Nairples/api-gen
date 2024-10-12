package com.nairples.apigen.model;

import java.util.List;

import lombok.Data;

@Data
public class Domain {
    private String name;
    private String packageName;
    private String description;
    private List<ClassDefinition> classes;
    private List<Relationship> relationships;
    private MavenConfiguration mavenConfiguration;
}
