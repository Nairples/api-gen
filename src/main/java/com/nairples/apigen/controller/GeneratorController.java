package com.nairples.apigen.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nairples.apigen.model.ApiGenReq;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Domain;
import com.nairples.apigen.model.MavenConfiguration;
import com.nairples.apigen.service.GeneratorClassService;
import com.nairples.apigen.service.GeneratorControllerService;
import com.nairples.apigen.service.GeneratorDomainService;
import com.nairples.apigen.service.GeneratorPomService;
import com.nairples.apigen.service.GeneratorRepositoryService;
import com.nairples.apigen.service.GeneratorServiceService;


@RestController
@RequestMapping("/api")
public class GeneratorController {
	
	@Autowired
	private GeneratorClassService classGenerator;
	
	@Autowired
	private GeneratorPomService pomGenerator;
	
	@Autowired
	private GeneratorDomainService domainGenerator;
	
	@Autowired
	private GeneratorControllerService controllerGenerator;
	
	@Autowired
	private GeneratorRepositoryService repositoryGenerator;
	
	@Autowired
	private GeneratorServiceService serviceGenerator;
	
	@PostMapping("/generate")
	public ResponseEntity<String> generator(@RequestBody ApiGenReq request) {
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}
	
	
	@PostMapping("/generate/domain")
	public ResponseEntity<String> generatorDomain(@RequestBody Domain request) {
		try {
			domainGenerator.generateDomain(request);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}
	
	
	@PostMapping("/generate/pom")
	public ResponseEntity<String> generatorPom(@RequestBody MavenConfiguration request) {
		pomGenerator.generatePomXmlFile("", "", request);
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}
	
	
	
	@PostMapping("/generate/class")
	public ResponseEntity<String> generatorClass(@RequestBody ClassDefinition request) {
		
		try {
			classGenerator.generateClass("", "", request);
			controllerGenerator.generateControllerClass("", "", request);
			repositoryGenerator.generateRepositoryInterface("", "", request);
			serviceGenerator.generateServiceClass("", "", request);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}

	

}
