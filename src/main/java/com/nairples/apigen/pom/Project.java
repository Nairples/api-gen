package com.nairples.apigen.pom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @XmlElement(name = "modelVersion")
    private String modelVersion = "4.0.0";

    @XmlElement(name = "groupId")
    private String groupId;


    @XmlElement(name = "artifactId")
    private String artifactId;

    @XmlElement(name = "version")
    private String version;

    @XmlElement(name = "packaging")
    private String packaging;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "url")
    private String url;

    @XmlElement(name = "parent")
    private Parent parent;

    @XmlElementWrapper(name = "dependencies")
    @XmlElement(name = "dependency")
    private List<Dependency> dependencies;

    @XmlElement(name = "build")
    private Build build;

}

