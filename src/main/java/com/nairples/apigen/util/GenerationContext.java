package com.nairples.apigen.util;

import com.nairples.apigen.model.ClassDefinition;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GenerationContext {
	
	private String projectName;
	private String domainName;
	private String packageName;
	
	public static GenerationContext getGenerationContext(ClassDefinition classDefinition) {
		return GenerationContext.builder()
        		.packageName(classDefinition.getPackageName())
        		.domainName("")
        		.projectName("")
        		.build();
	}
	
	public static GenerationContext getEmptyGenerationContext() {
		return GenerationContext.builder()
        		.packageName("")
        		.domainName("")
        		.projectName("")
        		.build();
	}
	
	public String getMainClassName() {
		return getDomainName()+"Application";
	}

}
