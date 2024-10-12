package com.nairples.apigen.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Domain;
import com.nairples.apigen.util.GenerationContext;

@Component
public class GeneratorDomainService {

	@Autowired
	private GeneratorClassService classGenerator;

	@Autowired
	private GeneratorRepositoryService repositoryGenerator;

	@Autowired
	private GeneratorControllerService controllerGenerator;

	@Autowired
	private GeneratorServiceService serviceGenerator;

	@Autowired
	private GeneratorPomService pomGenerator;
	
	@Autowired
	private GeneratorMainClassService mainGenerator;
	
	public void generateDomain(GenerationContext context, Domain domain) throws ClassNotFoundException, IOException {
		
		mainGenerator.generateMainClass(context);
		
		for (ClassDefinition classDefinition : domain.getClasses()) {
			classGenerator.generateClass(context, classDefinition);
			controllerGenerator.generateControllerClass(context, classDefinition);
			repositoryGenerator.generateRepositoryInterface(context, classDefinition);
			serviceGenerator.generateServiceClass(context, classDefinition);
		}

		pomGenerator.generateDefaultPomFile(context, domain);
	}

}
