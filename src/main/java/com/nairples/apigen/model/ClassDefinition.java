package com.nairples.apigen.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
public class ClassDefinition {
    private String name;
    private boolean isAbstract;
    private boolean isFinal;
    private String type;
    private boolean noArgsConstructor;
    private boolean fieldsConstructor;
    private String accessModifier;
    private String extendsClass;
    @Singular
    private List<String> implementsInterfaces;
    @Singular
    private List<Field> fields;
    @Singular
    private List<Method> methods;
    private String packageName;
    @Singular
    private List<Annotation> annotations;
    
}

