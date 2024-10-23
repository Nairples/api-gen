package com.nairples.apigen.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodeBlock {
	private String code;
	private Object arguments[];
	

}
