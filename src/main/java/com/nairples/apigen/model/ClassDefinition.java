package com.nairples.apigen.model;

import lombok.Data;
import java.util.List;

@Data
public class ClassDefinition {
    private String name;
    private boolean isAbstract;
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

