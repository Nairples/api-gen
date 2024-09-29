package com.nairples.apigen.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.lang.model.element.Modifier;

import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Component;

import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.model.Method;

@Component
public class GeneratorClassService {
	
	public void generateClass(ClassDefinition classDefinition) throws ClassNotFoundException, IOException {
		
		ArrayList<FieldSpec> fields = new ArrayList<>();
		if(classDefinition.getFields() != null ) {
			for (Field field: classDefinition.getFields()) {
				field.getName();
				FieldSpec fieldSpec = FieldSpec.builder(Class.forName(field.getType()), field.getName()).build();
				fields.add(fieldSpec);
			}
		}
		
		ArrayList<MethodSpec> methods = new ArrayList<>();
		if(classDefinition.getMethods() != null) {
			for (Method method: classDefinition.getMethods()) {

		        MethodSpec mSpec = MethodSpec.methodBuilder(method.getName())
		                .addModifiers(Modifier.PUBLIC)
		                .returns(Class.forName(method.getReturnType()))
		                .addCode("return null;\n")
		                .build();
		        
		        methods.add(mSpec);
			}
		}
		
		
		TypeSpec definedClass = TypeSpec.classBuilder(classDefinition.getName())
                .addModifiers(Modifier.PUBLIC)
                .build();
		
		for(FieldSpec fs: fields) {
			definedClass = definedClass
					.toBuilder()
					.addField(fs)
					.build();
			
		}

        JavaFile javaFile = JavaFile.builder("com.example", definedClass)
                .build();

        javaFile.writeTo(Paths.get("src/main/java")); 
		
	}

}
