package com.nairples.apigen.model;

import lombok.Data;

@Data
public class Field {
    private String name;
    private String type;
    private boolean isStatic;
    private boolean isFinal;
    private String accessModifier;
    private boolean get;
    private boolean set;
}
