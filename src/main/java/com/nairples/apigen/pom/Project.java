package com.nairples.apigen.pom;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "project", namespace = "http://maven.apache.org/POM/4.0.0")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @XmlElement(name = "modelVersion", namespace = "http://maven.apache.org/POM/4.0.0")
    private String modelVersion = "4.0.0";

    @XmlElement(name = "groupId", namespace = "http://maven.apache.org/POM/4.0.0")
    private String groupId;


    @XmlElement(name = "artifactId", namespace = "http://maven.apache.org/POM/4.0.0")
    private String artifactId;

    @XmlElement(name = "version", namespace = "http://maven.apache.org/POM/4.0.0")
    private String version;

    @XmlElement(name = "packaging", namespace = "http://maven.apache.org/POM/4.0.0")
    private String packaging;

    @XmlElement(name = "name", namespace = "http://maven.apache.org/POM/4.0.0")
    private String name;

    @XmlElement(name = "description", namespace = "http://maven.apache.org/POM/4.0.0")
    private String description;

    @XmlElement(name = "url", namespace = "http://maven.apache.org/POM/4.0.0")
    private String url;

    @XmlElement(name = "parent", namespace = "http://maven.apache.org/POM/4.0.0")
    private Parent parent;

    @XmlElementWrapper(name = "dependencies", namespace = "http://maven.apache.org/POM/4.0.0")
    @XmlElement(name = "dependency")
    private List<Dependency> dependencies;

    @XmlElement(name = "build", namespace = "http://maven.apache.org/POM/4.0.0")
    private Build build;
    
    @XmlElement(name = "properties", namespace = "http://maven.apache.org/POM/4.0.0")
    private Properties properties;

}

