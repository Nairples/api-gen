package com.nairples.apigen.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder(toBuilder = true)
public class Field {
    private String name;
    private String type;
    private boolean isStatic;
    private boolean isFinal;
    private String accessModifier;
    private boolean get;
    private boolean set;
    @Singular
    private List<Annotation> annotations;
}
