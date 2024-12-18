package com.nairples.apigen.controller;

import java.io.IOException;

import com.nairples.apigen.docker.DockerConfiguration;
import com.nairples.apigen.service.*;
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
import com.nairples.apigen.util.GenerationContext;


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
	private DockerConfigGenerator dockerConfigGenerator;
	
	@PostMapping("/generate")
	public ResponseEntity<String> generator(@RequestBody ApiGenReq request) {
		
		
		if(request != null && request.getDomains() != null ) {
			for (Domain domain : request.getDomains()) {
				try {
					GenerationContext context = GenerationContext.builder()
							.domainName(domain.getName())
							.packageName(domain.getPackageName())
							.projectName(request.getProjectName())
							.build();
					domainGenerator.generate(context, domain, request.getConfigurations());
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return new ResponseEntity<>("", HttpStatus.CREATED);
			}
		}
		
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}
	
	
	@PostMapping("/generate/domain")
	public ResponseEntity<String> generatorDomain(@RequestBody Domain request) {
		try {
			GenerationContext context = GenerationContext.builder()
					.domainName(request.getName())
					.packageName(request.getPackageName())
					.projectName("")
					.build();
			domainGenerator.generate(context, request);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}
	
	
	@PostMapping("/generate/pom")
	public ResponseEntity<String> generatorPom(@RequestBody MavenConfiguration request) {
		pomGenerator.generatePomXmlFile(GenerationContext.getEmptyGenerationContext(), request);
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}
	@PostMapping("/generate/docker-files")
	public ResponseEntity<String> generatorDockerConfigs(@RequestBody DockerConfiguration request) {
		dockerConfigGenerator.generateDockerConfigs(request);
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}
	
	
	
	@PostMapping("/generate/class")
	public ResponseEntity<String> generatorClass(@RequestBody ClassDefinition request) {
		
		try {
			GenerationContext generationContext = GenerationContext.getGenerationContext(request);
			classGenerator.generate(generationContext, request);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<>("", HttpStatus.CREATED);
	}

	

}
