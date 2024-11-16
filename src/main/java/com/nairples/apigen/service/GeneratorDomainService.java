package com.nairples.apigen.service;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nairples.apigen.config.ApiGenConfig;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Configurations;
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
	private GeneratorApplicationPropertiesService appPropertiesGenerator;
	
	@Autowired
	private GeneratorMainClassService mainGenerator;
	
	@Autowired
	private GeneratorDBEntityService dbEntityService;
	
	public void generate(GenerationContext context, Domain domain) throws ClassNotFoundException, IOException {
		
		generate(context, domain, null);
	}

	public void generate(GenerationContext context, Domain domain, Configurations configurations)  throws ClassNotFoundException, IOException  {
		// TODO Auto-generated method stub
		
		mainGenerator.generate(context);
		
		for (ClassDefinition classDefinition : domain.getClasses()) {
			classDefinition.setPackageName(context.getPackageName()+".model");
			classGenerator.generate(context, classDefinition);
			ClassDefinition dbEntity = dbEntityService.generate(context, classDefinition);
			ClassDefinition repositoryInterface = repositoryGenerator.generate(context, dbEntity);
			ClassDefinition classService = serviceGenerator.generate(context, classDefinition, dbEntity, repositoryInterface);
			controllerGenerator.generate(context, classDefinition, classService);
		}
		
		if(configurations == null) {
			configurations = new Configurations();
			configurations.setH2Database(true);
		}

		pomGenerator.generate(context, domain, configurations);
		
		
		appPropertiesGenerator.generate(context, configurations);
		
	}

}
