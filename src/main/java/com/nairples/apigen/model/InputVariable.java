package com.nairples.apigen.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InputVariable {
    private String name;
    private String className;
    private String packageName;
}
