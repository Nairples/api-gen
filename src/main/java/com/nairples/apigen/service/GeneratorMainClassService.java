package com.nairples.apigen.service;

import java.io.IOException;
import java.util.Collections;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

import com.nairples.apigen.model.Annotation;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.CodeBlock;
import com.nairples.apigen.model.InputVariable;
import com.nairples.apigen.model.Method;
import com.nairples.apigen.util.GenerationContext;



@Component
public class GeneratorMainClassService {
	
	@Autowired
	private GeneratorClassService generatorClass;

	
	
	public void generateMainClass( GenerationContext context) throws ClassNotFoundException, IOException {
		String className = context.getDomainName()+"Application";
		
		InputVariable inputParam = new InputVariable();
		inputParam.setName("args");
		inputParam.setType("String[]");
		ClassDefinition mainClassDefinition = ClassDefinition.builder()
				.name(className)
				.packageName("")
				.annotation(Annotation.builder()
						.packageName("org.springframework.boot.autoconfigure")
						.name("SpringBootApplication")
						.build())
				.accessModifier(Modifier.PUBLIC.name().toLowerCase())
				.method(Method
						.builder()
						.isStatic(true)
						.name("main")
						.accessModifier(Modifier.PUBLIC.name().toLowerCase())
						.code(CodeBlock.builder()
								.code("$T.run($L.class, args);")
								.arguments(new Object[]{SpringApplication.class, className})
								.build())
						.returnType("void")
						.inputVariables(Collections.singletonList(inputParam ))
						.build()
						)
				.build();
		generatorClass.generateClass(context, mainClassDefinition);
	
	}
}
