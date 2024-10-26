package com.nairples.apigen.service;

import java.io.IOException;
import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;


import com.nairples.apigen.model.Annotation;
import com.nairples.apigen.model.AnnotationMember;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.util.CustomStringUtils;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorControllerService {
	
	@Autowired
	private GeneratorClassService generatorClass;

	public void generateControllerClass(GenerationContext context, ClassDefinition classDefinition, ClassDefinition classDefinitionService) throws IOException, ClassNotFoundException {
		
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
				.field(Field.builder()
						.type(classDefinitionService.getName())
						.name(CustomStringUtils.uncapitalizeFirstLetter(classDefinitionService.getName()))
						.accessModifier(Modifier.PRIVATE.name().toLowerCase())
						.annotation(Annotation
								.builder()
								.name("Autowired")
								.packageName("org.springframework.web.bind.annotation")
								.build())
						.build())
				.build();
		generatorClass.generateClass(context, classService);
		
	}

}
