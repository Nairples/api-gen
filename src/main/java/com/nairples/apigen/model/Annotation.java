package com.nairples.apigen.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class Annotation {

	private String name;
	private String packageName;
	@Singular
	private List<AnnotationMember> members;
}
