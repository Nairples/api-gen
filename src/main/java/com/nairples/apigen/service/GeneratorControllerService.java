package com.nairples.apigen.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;


import com.nairples.apigen.model.Annotation;
import com.nairples.apigen.model.AnnotationMember;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorControllerService {
	
	@Autowired
	private GeneratorClassService generatorClass;

	public void generateControllerClass(GenerationContext context, ClassDefinition classDefinition) throws IOException, ClassNotFoundException {
		
		ClassDefinition classService = classDefinition
				.toBuilder()
				.packageName("controller")
				.name(classDefinition.getName()+"Controller")
				.clearAnnotations()
				.annotation(Annotation
						.builder()
						.name("RestController")
						.packageName("org.springframework.web.bind.annotation")
						.build())
				.annotation(Annotation
						.builder()
						.name("RequestMapping")
						.packageName("org.springframework.web.bind.annotation")
						.member(AnnotationMember
								.builder()
								.memberName("value")
								.memberValue("/"+ classDefinition.getName().toLowerCase())
								.build())
						.build())
				.clearMethods()
				.clearFields()
				.clearImplementsInterfaces()
				.build();
		generatorClass.generateClass(context, classService);
		
	}

}
