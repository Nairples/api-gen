package com.nairples.apigen.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Component;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.Domain;
import com.nairples.apigen.model.MavenConfiguration;
import com.nairples.apigen.model.MavenConfiguration.Properties;
import com.nairples.apigen.pom.Build;
import com.nairples.apigen.pom.Dependency;
import com.nairples.apigen.pom.Parent;
import com.nairples.apigen.pom.Plugin;
import com.nairples.apigen.pom.Plugins;
import com.nairples.apigen.pom.Project;

@Component
public class GeneratorPomService extends Generator {
	
	
	protected GeneratorPomService(ApiGenConfig apiGenConfig) {
		super(apiGenConfig);
		// TODO Auto-generated constructor stub
	}


	public void generateDefaultPomFile(Domain domain) {
		
		MavenConfiguration mavenConfiguration = new MavenConfiguration();
		mavenConfiguration.setArtifactId(domain.getName());
		mavenConfiguration.setGroupId(domain.getPackageName());
		mavenConfiguration.setVersion("0.0.1-SNAPSHOT");
		mavenConfiguration.setDescription(domain.getDescription());
		mavenConfiguration.setSpringBootVersion("3.3.4");
		List<com.nairples.apigen.model.MavenConfiguration.Plugin> plugins = new ArrayList<>();
		com.nairples.apigen.model.MavenConfiguration.Plugin pl = new com.nairples.apigen.model.MavenConfiguration.Plugin();
		pl.setGroupId("org.springframework.boot");
		pl.setArtifactId("spring-boot-maven-plugin");
		plugins.add(pl);
		mavenConfiguration.setPlugins(plugins );
		Properties properties = new Properties();
		properties.setJavaVersion("17");
		mavenConfiguration.setProperties(properties );
		
		List<com.nairples.apigen.model.MavenConfiguration.Dependency> dependencies = new ArrayList<>();
		com.nairples.apigen.model.MavenConfiguration.Dependency dep = new com.nairples.apigen.model.MavenConfiguration.Dependency();
		dep.setGroupId("org.springframework.boot");
		dep.setArtifactId("spring-boot-starter");
		// dep.setVersion("");
		dependencies.add(dep );
		com.nairples.apigen.model.MavenConfiguration.Dependency dep1 = new com.nairples.apigen.model.MavenConfiguration.Dependency();
		dep1.setGroupId("org.springframework.boot");
		dep1.setArtifactId("spring-boot-starter-web");
		// dep1.setVersion("");
		dependencies.add(dep1 );
		com.nairples.apigen.model.MavenConfiguration.Dependency dep2 = new com.nairples.apigen.model.MavenConfiguration.Dependency();
		dep2.setGroupId("org.projectlombok");
		dep2.setArtifactId("lombok");
		// dep2.setVersion("");
		dependencies.add(dep2 );
		
		com.nairples.apigen.model.MavenConfiguration.Dependency dep3 = new com.nairples.apigen.model.MavenConfiguration.Dependency();
		dep3.setGroupId("org.springframework.boot");
		dep3.setArtifactId("spring-boot-starter-data-jpa");
		// dep3.setVersion("");
		dependencies.add(dep3 );
		mavenConfiguration.setDependencies(dependencies );
		generatePomXmlFile("", domain.getName(), mavenConfiguration);
		
		
	}
	
	
	public void generatePomXmlFile(String projectName, String domainName, MavenConfiguration mavenConfiguration) {
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
			marshaller.marshal(project, new File(getPath(projectName, domainName) + "pom.xml"));
		}catch (Exception e){
			//TODO
		}

	}

}
