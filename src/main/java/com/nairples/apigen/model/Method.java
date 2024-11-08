package com.nairples.apigen.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class Method {
    private String name;
    private ClassDefinition returnType;
    private String accessModifier;
    private boolean isStatic;
    private boolean isFinal;
    private List<String> throwsList;
    private List<InputVariable> inputVariables;
    private CodeBlock code;
    @Singular
    private List<Annotation> annotations;
}
