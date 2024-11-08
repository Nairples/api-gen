package com.nairples.apigen.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.nairples.apigen.model.Annotation;
import com.nairples.apigen.model.AnnotationMember;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.CodeBlock;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.InputVariable;
import com.nairples.apigen.model.Method;
import com.nairples.apigen.util.CustomStringUtils;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorControllerService {

	@Autowired
	private GeneratorClassService generatorClass;

	public void generateControllerClass(GenerationContext context, ClassDefinition classDefinition,
			ClassDefinition classDefinitionService) throws IOException, ClassNotFoundException {

		String serviceInstance = CustomStringUtils.uncapitalizeFirstLetter(classDefinitionService.getName());
		String classInstance = CustomStringUtils.uncapitalizeFirstLetter(classDefinition.getName());

		ClassDefinition classController = classDefinition.toBuilder()
				.packageName(context.getPackageName() + ".controller").name(classDefinition.getName() + "Controller")
				.clearAnnotations()
				.annotation(Annotation.builder().name("RestController")
						.packageName("org.springframework.web.bind.annotation").build())
				.annotation(Annotation.builder().name("RequestMapping")
						.packageName("org.springframework.web.bind.annotation")
						.member(AnnotationMember.builder().memberName("value")
								.memberValue("/" + classDefinition.getName().toLowerCase()).build())
						.build())
				.clearMethods().clearFields().clearImplementsInterfaces()
				.field(Field.builder().className(classDefinitionService.getName())
						.name(CustomStringUtils.uncapitalizeFirstLetter(classDefinitionService.getName()))
						.accessModifier(Modifier.PRIVATE.name().toLowerCase())
						.packageName(classDefinitionService.getPackageName()).annotation(Annotation.builder()
								.name("Autowired").packageName("org.springframework.beans.factory.annotation").build())
						.build())
				.build();

		// Metodo CREATE (POST)
		Method createMethod = Method.builder().accessModifier(Modifier.PUBLIC.name().toLowerCase())
				.name("create" + classDefinition.getName())
				.annotation(Annotation.builder().name("PostMapping")
						.packageName("org.springframework.web.bind.annotation").build())
				.inputVariables(Collections.singletonList(InputVariable.builder().className(classDefinition.getName())
						.packageName(classDefinition.getPackageName()).name(classInstance).build()))
				.returnType(classDefinition)
				.code(CodeBlock.builder().code(
						"return " + serviceInstance + ".save" + classDefinition.getName() + "(" + classInstance + ");")
						.build())
				.build();

		// Metodo READ (GET by ID)
		Method getByIdMethod = Method.builder().accessModifier(Modifier.PUBLIC.name().toLowerCase())
				.name("get" + classDefinition.getName() + "ById")
				.annotation(Annotation.builder().name("GetMapping")
						.packageName("org.springframework.web.bind.annotation")
						.member(AnnotationMember.builder().memberName("value").memberValue("/{id}").build()).build())
				.inputVariables(Collections
						.singletonList(InputVariable.builder().className("Long").packageName("").name("id").build()))
				.returnType(classDefinition)
				.code(CodeBlock.builder().code("return " + serviceInstance + ".find"+ classDefinition.getName() +"ById(id);").build()).build();

		// Metodo UPDATE (PUT)
		Method updateMethod = Method.builder().accessModifier(Modifier.PUBLIC.name().toLowerCase())
				.name("update" + classDefinition.getName())
				.annotation(Annotation.builder().name("PutMapping")
						.packageName("org.springframework.web.bind.annotation")
						.member(AnnotationMember.builder().memberName("value").memberValue("/{id}").build()).build())
				.inputVariables(List.of(InputVariable.builder().className("Long").packageName("").name("id").build(),
						InputVariable.builder().className(classDefinition.getName())
								.packageName(classDefinition.getPackageName()).name(classInstance).build()))
				.returnType(classDefinition).code(CodeBlock.builder().code("return " + serviceInstance + ".update"
						+ classDefinition.getName() + "(id, " + classInstance + ");").build())
				.build();

		// Metodo DELETE (DELETE by ID)
		Method deleteMethod = Method.builder().accessModifier(Modifier.PUBLIC.name().toLowerCase())
				.name("delete" + classDefinition.getName())
				.annotation(Annotation.builder().name("DeleteMapping")
						.packageName("org.springframework.web.bind.annotation")
						.member(AnnotationMember.builder().memberName("value").memberValue("/{id}").build()).build())
				.inputVariables(Collections
						.singletonList(InputVariable.builder().className("Long").packageName("").name("id").build()))
				.returnType(ClassDefinition.builder().name("void").build()).code(CodeBlock.builder()
						.code(serviceInstance + ".delete" + classDefinition.getName() + "(id);").build())
				.build();

		// Aggiunta dei metodi al controller
		classController = classController.toBuilder().method(createMethod).method(getByIdMethod).method(updateMethod)
				.method(deleteMethod).build();

		generatorClass.generateClass(context, classController);

	}

}
