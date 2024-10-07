package com.nairples.apigen.model;

import java.util.List;

import lombok.Data;

@Data
public class Relationship {
    private String name;
    private String firstClassName;
    private String secondClassName;
    private String relationType;
    private boolean bidirectional;
    private String foreignKey;
    private List<Field> fields;
}

