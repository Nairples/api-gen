package com.nairples.apigen.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.Configurations;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorApplicationPropertiesService  extends Generator {

	protected GeneratorApplicationPropertiesService(ApiGenConfig apiGenConfig) {
		super(apiGenConfig);
		// TODO Auto-generated constructor stub
	}
	
	public void generate(GenerationContext context, Configurations configurations) {
		Properties properties = new Properties();
		if(configurations != null && configurations.isH2Database()) {
			properties.setProperty("spring.datasource.url", "jdbc:h2:mem:testdb");
			properties.setProperty("spring.datasource.driverClassName", "org.h2.Driver");
			properties.setProperty("spring.datasource.username", "sa");
			properties.setProperty("spring.datasource.password", "password");
			properties.setProperty("spring.datasource.platform", "h2");
			properties.setProperty("spring.h2.console.enabled", "true");
			properties.setProperty("spring.h2.console.path", "/h2-console");
			properties.setProperty("spring.jpa.database-platform", "org.hibernate.dialect.H2Dialect");
			properties.setProperty("spring.jpa.hibernate.ddl-auto", "update");
		}
		Path filePath = Paths.get(getResourcesPath(context) + "application.properties");
	    
	    try {
	       
	        Path parentDir = filePath.getParent();
	        if (parentDir != null && !Files.exists(parentDir)) {
	            Files.createDirectories(parentDir);  // Crea tutte le directory necessarie
	        }

	        try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
	        	properties.store(outputStream, "Application Configuration");
	            System.out.println("Properties written to the file successfully.");
	        }
	    } catch (IOException e) {
	        System.out.println("An error occurred while writing properties to the file.");
	        e.printStackTrace();
	    }
	}

}
