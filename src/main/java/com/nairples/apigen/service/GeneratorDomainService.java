package com.nairples.apigen.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Domain;

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
	
	public void generateDomain(Domain domain) throws ClassNotFoundException, IOException {
		String domainName = domain.getName();
		
		
		mainGenerator.generateMainClass("", domainName, domain.getPackageName());
		
		for (ClassDefinition classDefinition : domain.getClasses()) {
			classGenerator.generateClass("", domainName, domain.getPackageName(), classDefinition);
			controllerGenerator.generateControllerClass("", domainName, domain.getPackageName(), classDefinition);
			repositoryGenerator.generateRepositoryInterface("", domainName, domain.getPackageName(), classDefinition);
			serviceGenerator.generateServiceClass("", domainName, domain.getPackageName(), classDefinition);
		}

		pomGenerator.generateDefaultPomFile(domain);
	}

}
