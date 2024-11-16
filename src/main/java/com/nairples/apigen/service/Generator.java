package com.nairples.apigen.service;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.javapoet.JavaFile;
import org.springframework.util.StringUtils;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.util.GenerationContext;

public class Generator {
	
	
	@Autowired
	private ApiGenConfig apiGenConfig;
	
	protected Generator(ApiGenConfig apiGenConfig) {
		this.apiGenConfig = apiGenConfig;
	}

	protected void writeFile(GenerationContext context, JavaFile javaFile) throws IOException {
		javaFile.writeTo(Paths.get(getJavaPath(context)));
	}
	
	protected String getPath(GenerationContext context) {
		String result = apiGenConfig.getOutputDirectory()+"/";
		if(StringUtils.hasLength(context.getProjectName())) {
			result = result + context.getProjectName() + "/";
		}
		if(StringUtils.hasLength(context.getDomainName())) {
			result = result + context.getDomainName() + "/";
		}
		return result;
	}
	
	protected String getResourcesPath(GenerationContext context) {
		return getPath(context)+"src/main/resources/";
	}
	
	private String getJavaPath(GenerationContext context) {
		return getPath(context)+"src/main/java";
	}
}
