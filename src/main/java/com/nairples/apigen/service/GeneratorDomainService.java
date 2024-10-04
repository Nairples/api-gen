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
	private GeneratorPomService pomGenerator;
	
	public void generateDomain(Domain domain) throws ClassNotFoundException, IOException {
		for (ClassDefinition classDefinition : domain.getClasses()) {
			classGenerator.generateClass(classDefinition);
			classGenerator.generateControllerClass(classDefinition);
			classGenerator.generateRepositoryInterface(classDefinition);
			classGenerator.generateServiceClass(classDefinition);
		}
		
		pomGenerator.generatePomXmlFile(domain.getMavenConfiguration());
	}

}
