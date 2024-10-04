package com.nairples.apigen.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.lang.model.element.Modifier;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.nairples.apigen.model.MavenConfiguration;
import com.nairples.apigen.pom.*;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.Method;

@Component
public class GeneratorClassService {

	public void generateClass(ClassDefinition classDefinition) throws ClassNotFoundException, IOException {

		ArrayList<FieldSpec> fields = new ArrayList<>();
		if (classDefinition.getFields() != null) {
			for (Field field : classDefinition.getFields()) {
				FieldSpec fieldSpec = FieldSpec.builder(Class.forName(field.getType()), field.getName()).build();
				fields.add(fieldSpec);
			}
		}

		ArrayList<MethodSpec> methods = new ArrayList<>();
		if (classDefinition.getMethods() != null) {
			for (Method method : classDefinition.getMethods()) {
				MethodSpec mSpec = MethodSpec.methodBuilder(method.getName())
						.addModifiers(Modifier.PUBLIC)
						.returns(Class.forName(method.getReturnType()))
						.addCode("return null;\n")
						.build();

				methods.add(mSpec);
			}
		}

		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classDefinition.getName())
				.addModifiers(Modifier.PUBLIC);


		for (FieldSpec fs : fields) {
			classBuilder.addField(fs);
		}

		for (MethodSpec ms : methods) {
			classBuilder.addMethod(ms);
		}


		TypeSpec definedClass = classBuilder.build();


		JavaFile javaFile = JavaFile.builder(classDefinition.getPackageName(), definedClass)
				.build();

		javaFile.writeTo(Paths.get("src/main/java"));
	}
	
	public void generateControllerClass(ClassDefinition classDefinition) throws IOException {
		
		AnnotationSpec requestMappingAnnotation = AnnotationSpec.builder(RequestMapping.class)
		        .addMember("value", "$S", "/"+ classDefinition.getName().toLowerCase())  
		        .build();
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classDefinition.getName()+"Controller")
				.addAnnotation(RestController.class)
				.addAnnotation(requestMappingAnnotation )
				.addModifiers(Modifier.PUBLIC);
		
		TypeSpec definedClass = classBuilder.build();


		JavaFile javaFile = JavaFile.builder(classDefinition.getPackageName()+".controller", definedClass)
				.build();

		javaFile.writeTo(Paths.get("src/main/java"));
		
	}
	
	
    public void generateRepositoryInterface(ClassDefinition classDefinition) throws IOException {
    	
    	TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(classDefinition.getName()+"Repository")
				.addModifiers(Modifier.PUBLIC);
		
		TypeSpec definedClass = classBuilder.build();


		JavaFile javaFile = JavaFile.builder(classDefinition.getPackageName()+".repository", definedClass)
				.build();

		javaFile.writeTo(Paths.get("src/main/java"));
		
	}
    
    public void generateServiceClass(ClassDefinition classDefinition) throws IOException {
    	
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classDefinition.getName()+"Service")
				.addModifiers(Modifier.PUBLIC);
		
		TypeSpec definedClass = classBuilder.build();


		JavaFile javaFile = JavaFile.builder(classDefinition.getPackageName()+".service", definedClass)
				.build();

		javaFile.writeTo(Paths.get("src/main/java"));
	}

}
