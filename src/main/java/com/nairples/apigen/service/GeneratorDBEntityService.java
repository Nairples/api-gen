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
	
	public ClassDefinition generate(GenerationContext context, ClassDefinition classDefinition) throws ClassNotFoundException, IOException {
		
		
		ClassDefinition dbEntityDefinition = classDefinition
				.toBuilder()
				.packageName(context.getPackageName()+".entity")
				.name(classDefinition.getName()+"Entity")
				.clearAnnotations()
				.clearMethods()
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
			boolean idMissing = true;
			for (Field field : dbEntityDefinition.getFields()) {
				if( "id".equals(field.getName())) {
					idMissing = false;
				}
				ArrayList<Annotation> annotations = new ArrayList<Annotation>();
				annotations.add(Annotation
						.builder()
						.packageName("jakarta.persistence")
						.name("Column")
						.build());
				field.setAnnotations(annotations);
			}
			
			if(idMissing) {
				Annotation annotationId = Annotation.builder()
						.name("Id")
						.packageName("jakarta.persistence")
						.build(); 
				Field field = Field.builder()
						.name("id")
						.className("Long")
						.get(true)
						.set(true)
						.annotation(annotationId)
						.build();
				ArrayList<Field> fields = new ArrayList<>(dbEntityDefinition.getFields());
				fields.add(field);
				dbEntityDefinition.setFields(fields);
			}
		}
		
		generatorClass.generate(context, dbEntityDefinition);
		
		return dbEntityDefinition;
		
		
	}

}
