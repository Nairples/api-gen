package com.nairples.apigen.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnnotationMember {

	private String memberName;
	private String memberValue;

}
