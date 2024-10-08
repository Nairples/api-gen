package com.nairples.apigen.service;

import java.io.IOException;
import java.nio.file.Paths;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.ClassDefinition;

@Component
public class GeneratorRepositoryService {

	@Autowired
	private ApiGenConfig apiGenConfig;

    public GeneratorRepositoryService(ApiGenConfig apiGenConfig) {
        this.apiGenConfig = apiGenConfig;
    }


    public void generateRepositoryInterface(ClassDefinition classDefinition) throws IOException {

		AnnotationSpec repositoryAnnotation = AnnotationSpec
				.builder(ClassName.get("org.springframework.stereotype", "Repository")).build();
		TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(classDefinition.getName() + "Repository")
				.addAnnotation(repositoryAnnotation)
				.addSuperinterface(ParameterizedTypeName.get(
						ClassName.get("org.springframework.data.jpa.repository", "JpaRepository"),
						ClassName.get(classDefinition.getPackageName(), classDefinition.getName()),
						ClassName.get(Long.class)))
				.addModifiers(Modifier.PUBLIC);

		TypeSpec definedClass = classBuilder.build();

		JavaFile javaFile = JavaFile.builder(classDefinition.getPackageName() + ".repository", definedClass).build();

		javaFile.writeTo(Paths.get(apiGenConfig.getOutputDirectory() + "src/main/java"));

	}

}
