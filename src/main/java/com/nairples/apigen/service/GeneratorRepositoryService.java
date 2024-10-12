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
public class GeneratorRepositoryService extends Generator {

	public GeneratorRepositoryService(ApiGenConfig apiGenConfig) {
        super(apiGenConfig);
    }


    public void generateRepositoryInterface(String projectName, String domainName, String packageName, ClassDefinition classDefinition) throws IOException {

		AnnotationSpec repositoryAnnotation = AnnotationSpec
				.builder(ClassName.get("org.springframework.stereotype", "Repository")).build();
		TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(classDefinition.getName() + "Repository")
				.addAnnotation(repositoryAnnotation)
				.addSuperinterface(ParameterizedTypeName.get(
						ClassName.get("org.springframework.data.jpa.repository", "JpaRepository"),
						ClassName.get(packageName, classDefinition.getName()),
						ClassName.get(Long.class)))
				.addModifiers(Modifier.PUBLIC);

		TypeSpec definedClass = classBuilder.build();

		JavaFile javaFile = JavaFile.builder(packageName + ".repository", definedClass).build();

		writeFile(projectName, domainName, javaFile);

	}

}
