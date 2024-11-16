package com.nairples.apigen.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Component;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.Configurations;
import com.nairples.apigen.model.Domain;
import com.nairples.apigen.model.MavenConfiguration;
import com.nairples.apigen.model.MavenConfiguration.Plugin.Configuration;
import com.nairples.apigen.model.MavenConfiguration.Properties;
import com.nairples.apigen.pom.Build;
import com.nairples.apigen.pom.Dependency;
import com.nairples.apigen.pom.Parent;
import com.nairples.apigen.pom.Plugin;
import com.nairples.apigen.pom.Plugins;
import com.nairples.apigen.pom.Project;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorPomService extends Generator {
	
	
	protected GeneratorPomService(ApiGenConfig apiGenConfig) {
		super(apiGenConfig);
		// TODO Auto-generated constructor stub
	}


	public void generate(GenerationContext context, Domain domain, Configurations configurations) {
		
		MavenConfiguration mavenConfiguration = new MavenConfiguration();
		mavenConfiguration.setArtifactId(domain.getName().toLowerCase());
		mavenConfiguration.setGroupId(domain.getPackageName());
		mavenConfiguration.setVersion("0.0.1-SNAPSHOT");
		mavenConfiguration.setDescription(domain.getDescription());
		mavenConfiguration.setSpringBootVersion("3.3.4");
		List<com.nairples.apigen.model.MavenConfiguration.Plugin> plugins = new ArrayList<>();
		com.nairples.apigen.model.MavenConfiguration.Plugin pl = new com.nairples.apigen.model.MavenConfiguration.Plugin();
		pl.setGroupId("org.springframework.boot");
		pl.setArtifactId("spring-boot-maven-plugin");
		Configuration configuration = new Configuration();
		configuration.setMainClass(context.getPackageName()+"."+context.getMainClassName());
		pl.setConfiguration(configuration);
		plugins.add(pl);
		mavenConfiguration.setPlugins(plugins );
		Properties properties = new Properties();
		properties.setJavaVersion("17");
		mavenConfiguration.setProperties(properties );

		List<MavenConfiguration.Dependency> dependencies = getDependencyList();
		
		if(configurations != null & configurations.isH2Database()) {
			com.nairples.apigen.model.MavenConfiguration.Dependency h2Dependency = new com.nairples.apigen.model.MavenConfiguration.Dependency();
			h2Dependency.setArtifactId("h2");
			h2Dependency.setGroupId("com.h2database");
			h2Dependency.setScope("runtime");
			dependencies.add(h2Dependency );
		}
		mavenConfiguration.setDependencies(dependencies );
		generatePomXmlFile(context, mavenConfiguration);
		
		
	}

	private static List<MavenConfiguration.Dependency> getDependencyList() {
		List<MavenConfiguration.Dependency> dependencies = new ArrayList<>();
		MavenConfiguration.Dependency dep = new MavenConfiguration.Dependency();
		dep.setGroupId("org.springframework.boot");
		dep.setArtifactId("spring-boot-starter");
		// dep.setVersion("");
		dependencies.add(dep );
		MavenConfiguration.Dependency dep1 = new MavenConfiguration.Dependency();
		dep1.setGroupId("org.springframework.boot");
		dep1.setArtifactId("spring-boot-starter-web");
		// dep1.setVersion("");
		dependencies.add(dep1 );
		MavenConfiguration.Dependency dep2 = new MavenConfiguration.Dependency();
		dep2.setGroupId("org.projectlombok");
		dep2.setArtifactId("lombok");
		// dep2.setVersion("");
		dependencies.add(dep2 );

		MavenConfiguration.Dependency dep3 = new MavenConfiguration.Dependency();
		dep3.setGroupId("org.springframework.boot");
		dep3.setArtifactId("spring-boot-starter-data-jpa");
		// dep3.setVersion("");
		dependencies.add(dep3 );
		return dependencies;
	}


	public void generatePomXmlFile(GenerationContext context, MavenConfiguration mavenConfiguration) {
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
											.configuration(plg.getConfiguration() != null && plg.getConfiguration().getMainClass() != null ? com.nairples.apigen.pom.Configuration.builder().mainClass(plg.getConfiguration().getMainClass()).build() : null )
											.build()
									).toList()
							).build()
					).build()
			).build();
			JAXBContext jaxBContext = JAXBContext.newInstance(Project.class);
			Marshaller marshaller = jaxBContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd");
			marshaller.marshal(project, new File(getPath(context) + "pom.xml"));
		}catch (Exception e){
			//TODO
			e.printStackTrace();
		}

	}

}
