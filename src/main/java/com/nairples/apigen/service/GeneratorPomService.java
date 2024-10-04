package com.nairples.apigen.service;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Component;

import com.nairples.apigen.model.MavenConfiguration;
import com.nairples.apigen.pom.Build;
import com.nairples.apigen.pom.Dependency;
import com.nairples.apigen.pom.Parent;
import com.nairples.apigen.pom.Plugin;
import com.nairples.apigen.pom.Plugins;
import com.nairples.apigen.pom.Project;

@Component
public class GeneratorPomService {
	
	
	public void generatePomXmlFile(MavenConfiguration mavenConfiguration) {
		try {
			Project.ProjectBuilder projectBuilder = Project.builder()
					.modelVersion("4.0.0")
					.groupId(mavenConfiguration.getGroupId())
					.artifactId(mavenConfiguration.getArtifactId())
					.version(mavenConfiguration.getVersion())
					.packaging(mavenConfiguration.getPackaging())
					.name(mavenConfiguration.getArtifactId())
					.description(mavenConfiguration.getDescription())
					.url(mavenConfiguration.getUrl())
					.dependencies(mavenConfiguration.getDependencies().stream()
							.map(dep -> Dependency.builder()
									.groupId(dep.getGroupId())
									.artifactId(dep.getArtifactId())
									.version(dep.getVersion())
									.scope(dep.getScope())
									.build()
							).toList()
					);


			if (mavenConfiguration.getSpringBootVersion() != null && !mavenConfiguration.getSpringBootVersion().isEmpty()) {
				Parent parent = Parent.builder()
						.groupId("org.springframework.boot")
						.artifactId("spring-boot-starter-parent")
						.version(mavenConfiguration.getSpringBootVersion())
						.relativePath("")
						.build();

				projectBuilder.parent(parent);
			}


			Project project = projectBuilder.build(Build.builder()
					.plugins(Plugins.builder()
							.plugins(mavenConfiguration.getPlugins().stream()
									.map(plg -> Plugin.builder()
											.groupId(plg.getGroupId())
											.artifactId(plg.getArtifactId())
											.version(plg.getVersion())
											.build()
									).toList()
							).build()
					).build()
			).build();
			JAXBContext context = JAXBContext.newInstance(Project.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");
			marshaller.marshal(project, new File("pom.xml"));
		}catch (Exception e){
			//TODO
		}

	}

}
