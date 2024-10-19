package com.nairples.apigen.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.Annotation;
import com.nairples.apigen.model.AnnotationMember;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorServiceService {

	@Autowired
	private GeneratorClassService classGenerator;

	

	public void generateServiceClass(GenerationContext context, ClassDefinition classDefinition, ClassDefinition dbEntity) throws IOException, ClassNotFoundException {

		
		ClassDefinition classService = classDefinition
				.toBuilder()
				.packageName("service")
				.name(classDefinition.getName()+"Service")
				.clearAnnotations()
				.clearMethods()
				.clearFields()
				.clearImplementsInterfaces()
				.build();
		classGenerator.generateClass(context, classService);
	}

}
