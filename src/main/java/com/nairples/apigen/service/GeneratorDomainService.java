package com.nairples.apigen.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	
	public void generateDomain(Domain domain) throws ClassNotFoundException, IOException {
		for (ClassDefinition classDefinition : domain.getClasses()) {
			classGenerator.generateClass(classDefinition);
			controllerGenerator.generateControllerClass(classDefinition);
			repositoryGenerator.generateRepositoryInterface(classDefinition);
			serviceGenerator.generateServiceClass(classDefinition);
		}
		
		pomGenerator.generatePomXmlFile(domain.getMavenConfiguration());
	}

}
