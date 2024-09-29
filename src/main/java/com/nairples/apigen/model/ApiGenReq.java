package com.nairples.apigen.model;

import java.util.List;

import lombok.Data;

@Data
public class ApiGenReq {
	
	private String projectName;
	private Configurations configurations;
    private List<Domain> domains;
	private List<Relationship> relationships;

}
