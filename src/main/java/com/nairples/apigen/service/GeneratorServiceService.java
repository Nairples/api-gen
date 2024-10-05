package com.nairples.apigen.service;

import java.io.IOException;
import java.nio.file.Paths;

import javax.lang.model.element.Modifier;

import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;

import com.nairples.apigen.model.ClassDefinition;


@Component
public class GeneratorServiceService {
	
	 public void generateServiceClass(ClassDefinition classDefinition) throws IOException {
	    	
			TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classDefinition.getName()+"Service")
					.addModifiers(Modifier.PUBLIC);
			
			TypeSpec definedClass = classBuilder.build();


			JavaFile javaFile = JavaFile.builder(classDefinition.getPackageName()+".service", definedClass)
					.build();

			javaFile.writeTo(Paths.get("src/main/java"));
		}

}
