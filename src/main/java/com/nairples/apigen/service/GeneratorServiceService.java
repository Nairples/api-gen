package com.nairples.apigen.service;

import java.io.IOException;
import java.nio.file.Paths;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorServiceService extends Generator {

	

	protected GeneratorServiceService(ApiGenConfig apiGenConfig) {
		super(apiGenConfig);
		// TODO Auto-generated constructor stub
	}

	public void generateServiceClass(GenerationContext context, ClassDefinition classDefinition) throws IOException {

		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classDefinition.getName() + "Service")
				.addModifiers(Modifier.PUBLIC);

		TypeSpec definedClass = classBuilder.build();

		JavaFile javaFile = JavaFile.builder(context.getPackageName() + ".service", definedClass).build();

		writeFile(context, javaFile);
	}

}
