package com.nairples.apigen.service;

import java.io.IOException;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nairples.apigen.model.Annotation;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorRepositoryService {
	
	@Autowired
	GeneratorClassService classGenerator;

	public ClassDefinition generateRepositoryInterface(GenerationContext context, ClassDefinition classDefinition) throws IOException, ClassNotFoundException {

		ClassDefinition repositoryInterface = classDefinition
				.toBuilder()
				.type("interface")
				.packageName(context.getPackageName()+".repository")
				.name(classDefinition.getName()+"Repository")
				.accessModifier(Modifier.PUBLIC.name().toLowerCase())
				.clearAnnotations()
				.clearMethods()
				.clearFields()
				.clearImplementsInterfaces()
				.implementsInterface(ClassDefinition.builder()
						.name("JpaRepository")
						.packageName("org.springframework.data.jpa.repository")
						.generic(ClassDefinition.builder()
								.name(classDefinition.getName())
								.packageName(classDefinition.getPackageName())
								.build())
						.generic(ClassDefinition.builder()
								.name("Long")
								.packageName("")
								.build())
						.build())
				.annotation(Annotation.builder()
						.name("Repository")
						.packageName("org.springframework.stereotype")
						.build())
				.build();
		classGenerator.generateClass(context, repositoryInterface);
		
		return repositoryInterface;

	}

}
