package com.nairples.apigen.model;

import lombok.Data;
import java.util.List;

@Data
public class Domain {
    private String name;
    private List<ClassDefinition> classes;
    private List<Relationship> relationships;
    private MavenConfiguration mavenConfiguration;
}
