package com.nairples.apigen.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nairples.apigen.model.Annotation;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorServiceService {

	@Autowired
	private GeneratorClassService classGenerator;

	

	public ClassDefinition generateServiceClass(GenerationContext context, ClassDefinition classDefinition, ClassDefinition dbEntity) throws IOException, ClassNotFoundException {
		ClassDefinition classService = classDefinition
				.toBuilder()
				.packageName("service")
				.name(classDefinition.getName()+"Service")
				.clearAnnotations()
				.clearMethods()
				.clearFields()
				.clearImplementsInterfaces()
				.annotation(Annotation.builder()
						.name("Component")
						.packageName("org.springframework.stereotype")
						.build())
				.build();
		classGenerator.generateClass(context, classService);
		return classService;
	}

}
