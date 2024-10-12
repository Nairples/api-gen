package com.nairples.apigen.service;

import java.io.IOException;

import javax.lang.model.element.Modifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.ParameterSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;


import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.util.GenerationContext;


@Component
public class GeneratorMainClassService extends Generator  {

	protected GeneratorMainClassService(ApiGenConfig apiGenConfig) {
		super(apiGenConfig);
		// TODO Auto-generated constructor stub
	}

	
	
	public void generateMainClass( GenerationContext context) throws ClassNotFoundException, IOException {
		String className = context.getDomainName()+"Application";
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
				.addAnnotation(SpringBootApplication.class)
				.addModifiers(Modifier.PUBLIC);
		
		MethodSpec methodSpec = MethodSpec.methodBuilder("main")
				.addCode("$T.run($L.class, args);", SpringApplication.class, className)
				.addModifiers(Modifier.PUBLIC)
				.addModifiers(Modifier.STATIC)
				.returns(void.class)
				.addParameter(ParameterSpec.builder(ClassName.get("", "String[]"), "args").build())
				.build();
		TypeSpec definedClass = classBuilder
				.addMethod(methodSpec )
				.build();


		JavaFile javaFile = JavaFile.builder(context.getPackageName(), definedClass)
				.build();

		writeFile(context, javaFile);
	
	}
}
