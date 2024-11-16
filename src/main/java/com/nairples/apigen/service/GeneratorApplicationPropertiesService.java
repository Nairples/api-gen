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
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorApplicationPropertiesService  extends Generator {

	protected GeneratorApplicationPropertiesService(ApiGenConfig apiGenConfig) {
		super(apiGenConfig);
		// TODO Auto-generated constructor stub
	}
	
	public void generate(GenerationContext context, Properties properties) {
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
