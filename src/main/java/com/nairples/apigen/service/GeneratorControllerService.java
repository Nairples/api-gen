package com.nairples.apigen.service;

import java.io.IOException;
import java.nio.file.Paths;

import javax.lang.model.element.Modifier;

import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nairples.apigen.model.ClassDefinition;

@Component
public class GeneratorControllerService {
	
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

}
