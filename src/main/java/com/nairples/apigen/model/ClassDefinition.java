package com.nairples.apigen.model;

import java.util.List;

import lombok.Data;

@Data
public class ClassDefinition {
    private String name;
    private boolean isAbstract;
    private boolean isFinal;
    private String type;
    private boolean noArgsConstructor;
    private boolean fieldsConstructor;
    private String accessModifier;
    private String extendsClass;
    private List<String> implementsInterfaces;
    private List<Field> fields;
    private List<Method> methods;
    private String packageName;
    
}

