package com.nairples.apigen.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.lang.model.element.Modifier;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.nairples.apigen.model.MavenConfiguration;
import com.nairples.apigen.pom.*;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.Method;

@Component
public class GeneratorClassService {

	public void generateClass(ClassDefinition classDefinition) throws ClassNotFoundException, IOException {

		ArrayList<FieldSpec> fields = new ArrayList<>();
		if (classDefinition.getFields() != null) {
			for (Field field : classDefinition.getFields()) {
				FieldSpec fieldSpec = FieldSpec.builder(Class.forName(field.getType()), field.getName()).build();
				fields.add(fieldSpec);
			}
		}

		ArrayList<MethodSpec> methods = new ArrayList<>();
		if (classDefinition.getMethods() != null) {
			for (Method method : classDefinition.getMethods()) {
				MethodSpec mSpec = MethodSpec.methodBuilder(method.getName())
						.addModifiers(Modifier.PUBLIC)
						.returns(Class.forName(method.getReturnType()))
						.addCode("return null;\n")
						.build();

				methods.add(mSpec);
			}
		}

		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classDefinition.getName())
				.addModifiers(Modifier.PUBLIC);


		for (FieldSpec fs : fields) {
			classBuilder.addField(fs);
		}

		for (MethodSpec ms : methods) {
			classBuilder.addMethod(ms);
		}


		TypeSpec definedClass = classBuilder.build();


		JavaFile javaFile = JavaFile.builder(classDefinition.getPackageName(), definedClass)
				.build();

		javaFile.writeTo(Paths.get("src/main/java"));
	}
	
	public void generateControllerClass(ClassDefinition classDefinition) throws IOException {
		
		AnnotationSpec requestMappingAnnotation = AnnotationSpec.builder(RequestMapping.class)
		        .addMember("value", "$S", "/"+ classDefinition.getName().toLowerCase())  
		        .build();
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classDefinition.getName()+"Controller")
				.addAnnotation(RestController.class)
				.addAnnotation(requestMappingAnnotation )
				.addModifiers(Modifier.PUBLIC);
		
		TypeSpec definedClass = classBuilder.build();


		JavaFile javaFile = JavaFile.builder(classDefinition.getPackageName()+".controller", definedClass)
				.build();

		javaFile.writeTo(Paths.get("src/main/java"));
		
	}
	
	
    public void generateRepositoryInterface(ClassDefinition classDefinition) throws IOException {
    	
    	TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(classDefinition.getName()+"Repository")
				.addModifiers(Modifier.PUBLIC);
		
		TypeSpec definedClass = classBuilder.build();


		JavaFile javaFile = JavaFile.builder(classDefinition.getPackageName()+".repository", definedClass)
				.build();

		javaFile.writeTo(Paths.get("src/main/java"));
		
	}
    
    public void generateServiceClass(ClassDefinition classDefinition) throws IOException {
    	
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classDefinition.getName()+"Service")
				.addModifiers(Modifier.PUBLIC);
		
		TypeSpec definedClass = classBuilder.build();


		JavaFile javaFile = JavaFile.builder(classDefinition.getPackageName()+".service", definedClass)
				.build();

		javaFile.writeTo(Paths.get("src/main/java"));
	}


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
