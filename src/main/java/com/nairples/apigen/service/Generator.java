package com.nairples.apigen.service;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.javapoet.JavaFile;
import org.springframework.util.StringUtils;

import com.nairples.apigen.config.ApiGenConfig;

public class Generator {
	
	
	@Autowired
	private ApiGenConfig apiGenConfig;
	
	protected Generator(ApiGenConfig apiGenConfig) {
		this.apiGenConfig = apiGenConfig;
	}

	protected void writeFile(String projectName, String domainName, JavaFile javaFile) throws IOException {
		javaFile.writeTo(Paths.get(getPath(projectName, domainName)+"src/main/java"));
	}
	
	protected String getPath(String projectName, String domainName) {
		String result = apiGenConfig.getOutputDirectory()+"/";
		if(StringUtils.hasLength(projectName)) {
			result = result + projectName + "/";
		}
		if(StringUtils.hasLength(domainName)) {
			result = result + domainName + "/";
		}
		return result;
	}
}
