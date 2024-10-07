package com.nairples.apigen.model;

import java.util.List;

import lombok.Data;

@Data
public class Method {
    private String name;
    private String returnType;
    private String accessModifier;
    private boolean isStatic;
    private boolean isFinal;
    private List<String> throwsList;
    private List<InputVariable> inputVariables;
    private String code;
}
