package com.nairples.apigen.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Method {
    private String name;
    private String returnType;
    private String accessModifier;
    private boolean isStatic;
    private boolean isFinal;
    private List<String> throwsList;
    private List<InputVariable> inputVariables;
    private CodeBlock code;
}
