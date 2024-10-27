package com.nairples.apigen.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nairples.apigen.model.Annotation;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.CodeBlock;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.InputVariable;
import com.nairples.apigen.model.Method;
import com.nairples.apigen.util.CustomStringUtils;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorServiceService {

	@Autowired
	private GeneratorClassService classGenerator;

	

	public ClassDefinition generateServiceClass(GenerationContext context, ClassDefinition classDefinition, ClassDefinition dbEntity, ClassDefinition repositoryInterface) throws IOException, ClassNotFoundException {
		String repositoryInstance = CustomStringUtils.uncapitalizeFirstLetter(repositoryInterface.getName());
		String dbEntityInstance = CustomStringUtils.uncapitalizeFirstLetter(dbEntity.getName());
		String classDefinitionInstance = CustomStringUtils.uncapitalizeFirstLetter(classDefinition.getName());
		
		ClassDefinition classService = classDefinition
				.toBuilder()
				.packageName(context.getPackageName()+".service")
				.name(classDefinition.getName()+"Service")
				.clearAnnotations()
				.clearMethods()
				.clearFields()
				.clearImplementsInterfaces()
				.annotation(Annotation.builder()
						.name("Component")
						.packageName("org.springframework.stereotype")
						.build())
				.field(Field.builder()
						.className(repositoryInterface.getName())
						.name(repositoryInstance)
						.accessModifier(Modifier.PRIVATE.name().toLowerCase())
						.packageName(repositoryInterface.getPackageName())
						.annotation(Annotation
								.builder()
								.name("Autowired")
								.packageName("org.springframework.beans.factory.annotation")
								.build())
						.build())
				.method(Method
						.builder()
						.accessModifier(Modifier.PRIVATE.name().toLowerCase())
						.name("convertToEntity")
						.inputVariables(Collections.singletonList(InputVariable
								.builder()
								.className(classDefinition.getName())
								.packageName(classDefinition.getPackageName())
								.name(classDefinitionInstance)
								.build()))
						.returnType(dbEntity)
						/*.code(CodeBlock
								.builder()
								.build())*/
						.build())
				.method(Method
						.builder()
						.accessModifier(Modifier.PRIVATE.name().toLowerCase())
						.name("convertToModel")
						.inputVariables(Collections.singletonList(InputVariable
								.builder()
								.className(dbEntity.getName())
								.packageName(dbEntity.getPackageName())
								.name(classDefinitionInstance)
								.build()))
						.returnType(classDefinition)
						.build())
				.method(Method
						.builder()
						.accessModifier(Modifier.PUBLIC.name().toLowerCase())
						.name("save"+classDefinition.getName())
						.inputVariables(Collections.singletonList(InputVariable
								.builder()
								.className(classDefinition.getName())
								.packageName(classDefinition.getPackageName())
								.name(classDefinitionInstance)
								.build()))
						.returnType(classDefinition)
						.code(CodeBlock
								.builder()
								.code(dbEntity.getName()+" "+dbEntityInstance+ " = convertToEntity("+classDefinitionInstance+");\n"
										+ dbEntityInstance+" = " + repositoryInstance+".save("+dbEntityInstance+");\n"
										+ classDefinition.getName()+" saved"+classDefinition.getName()+ " = convertToModel("+dbEntityInstance+");\n"
										+ "return saved"+classDefinition.getName()+";")
								.build())
						.build())
				.method(Method
						.builder()
						.accessModifier(Modifier.PUBLIC.name().toLowerCase())
						.name("delete"+classDefinition.getName())
						.inputVariables(Collections.singletonList(InputVariable
								.builder()
								.className("Long")
								.packageName("")
								.name("id")
								.build()))
						.returnType(ClassDefinition.builder().name("void").build())
						.code(CodeBlock.builder().code(repositoryInstance+".deleteById(id);").build())
						.build())
				.method(Method
						.builder()
						.accessModifier(Modifier.PUBLIC.name().toLowerCase())
						.name("update"+classDefinition.getName())
						.inputVariables(List.of(
								InputVariable
								.builder()
								.className("Long")
								.packageName("")
								.name("id")
								.build(),
								InputVariable
								.builder()
								.className(classDefinition.getName())
								.packageName(classDefinition.getPackageName())
								.name(CustomStringUtils.uncapitalizeFirstLetter(classDefinition.getName()))
								.build()
								))
						.returnType(classDefinition)
						.build())
				.build();
		classGenerator.generateClass(context, classService);
		return classService;
	}

}
