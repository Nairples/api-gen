package com.nairples.apigen.config;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class ApiGenConfig {
	
	@Value("${apiGen.outputDirectory}")
	private String outputDirectory;



	public String getOutputDirectory() {
		
		if(!StringUtils.hasLength(outputDirectory)) {
			String userHome = System.getProperty("user.home");
			String timestamp = getCurrentTimestamp();
			
			outputDirectory = userHome + File.separator + "apiGen" +
					File.separator + 
					timestamp +
					File.separator;
		}
		return outputDirectory;
	}

	 private String getCurrentTimestamp() {
	        
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	        return LocalDateTime.now().format(formatter);
	    }
	
	

}
