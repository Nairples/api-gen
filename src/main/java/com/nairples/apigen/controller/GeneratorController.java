package com.nairples.apigen.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nairples.apigen.model.ApiGenReq;
import com.nairples.apigen.model.ClassDefinition;
import com.nairples.apigen.model.Domain;
import com.nairples.apigen.model.MavenConfiguration;
import com.nairples.apigen.service.GeneratorClassService;
import com.nairples.apigen.service.GeneratorPomService;


@RestController
@RequestMapping("/api")
public class GeneratorController {
	
	@Autowired
	private GeneratorClassService generatorClassService;
	
	@Autowired
	private GeneratorPomService generatorPomService;
	
	@Autowired
	private GeneratorPomService generatorDomainService;
	
	@PostMapping("/generate")
	public ResponseEntity<String> generator(@RequestBody ApiGenReq request) {
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}
	
	
	@PostMapping("/generate/domain")
	public ResponseEntity<String> generatorDomain(@RequestBody Domain request) {
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}
	
	
	@PostMapping("/generate/pom")
	public ResponseEntity<String> generatorPom(@RequestBody MavenConfiguration request) {
		generatorPomService.generatePomXmlFile(request);
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}
	
	
	
	@PostMapping("/generate/class")
	public ResponseEntity<String> generatorClass(@RequestBody ClassDefinition request) {
		
		try {
			generatorClassService.generateClass(request);
			generatorClassService.generateControllerClass(request);
			generatorClassService.generateRepositoryInterface(request);
			generatorClassService.generateServiceClass(request);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}

	

}
