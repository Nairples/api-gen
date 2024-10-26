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
	
	@Autowired
	private GeneratorDBEntityService dbEntityService;
	
	public void generateDomain(GenerationContext context, Domain domain) throws ClassNotFoundException, IOException {
		
		mainGenerator.generateMainClass(context);
		
		for (ClassDefinition classDefinition : domain.getClasses()) {
			classDefinition.setPackageName("model");
			classGenerator.generateClass(context, classDefinition);
			ClassDefinition dbEntity = dbEntityService.generateDBEntity(context, classDefinition);
			repositoryGenerator.generateRepositoryInterface(context, dbEntity);
			ClassDefinition classService = serviceGenerator.generateServiceClass(context, classDefinition, dbEntity);
			controllerGenerator.generateControllerClass(context, classDefinition, classService);
		}

		pomGenerator.generateDefaultPomFile(context, domain);
	}

}
