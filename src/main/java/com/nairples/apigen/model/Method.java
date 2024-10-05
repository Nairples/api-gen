package com.nairples.apigen.model;

import lombok.Data;
import java.util.List;

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
