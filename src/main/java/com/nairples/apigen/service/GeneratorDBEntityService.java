package com.nairples.apigen.service;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nairples.apigen.model.Annotation;
import com.nairples.apigen.model.AnnotationMember;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Field;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorDBEntityService {
	
	@Autowired
	private GeneratorClassService generatorClass;
	
	public ClassDefinition generateDBEntity(GenerationContext context, ClassDefinition classDefinition) throws ClassNotFoundException, IOException {
		
		
		ClassDefinition dbEntityDefinition = classDefinition
				.toBuilder()
				.packageName("entity")
				.name(classDefinition.getName()+"Entity")
				.annotations(new ArrayList<Annotation>())
				.annotation(Annotation.builder()
						.packageName("jakarta.persistence")
						.name("Entity")
						.build())
				.annotation(Annotation.builder()
						.packageName("jakarta.persistence")
						.name("Table")
						.member(AnnotationMember
								.builder()
								.memberName("name")
								.memberValue(classDefinition.getName()+"Entity")
								.build())
						.build())
				.build();
		
		
		
		if(dbEntityDefinition.getFields() != null ) {
			for (Field field : dbEntityDefinition.getFields()) {
				field.setAnnotations(null);
				field = field
						.toBuilder()
						.annotation(Annotation
								.builder()
								.packageName("jakarta.persistence")
								.name("Column")
								.build())
						.build();
			}
		}
		
		generatorClass.generateClass(context, dbEntityDefinition);
		
		return dbEntityDefinition;
		
		
	}

}
