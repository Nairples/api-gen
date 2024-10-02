package com.nairples.apigen.pom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dependency")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dependency {

    @XmlElement(name = "groupId")
    private String groupId;

    @XmlElement(name = "artifactId")
    private String artifactId;

    @XmlElement(name = "version")
    private String version;

    @XmlElement(name = "scope")
    private String scope;

}

