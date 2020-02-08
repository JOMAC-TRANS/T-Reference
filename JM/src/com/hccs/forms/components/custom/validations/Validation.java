package com.hccs.forms.components.custom.validations;

import com.hccs.forms.components.custom.constants.ValidationConstants;

public abstract class Validation { 
    private ValidationConstants validationType;
    private final String name;

    public Validation(String name) {
        this.name = name;
    }
    
    public abstract String validate();

    public String getName() {
        return name;
    }

    public ValidationConstants getValidationType() {
        return validationType;
    }

    protected void setValidationType(ValidationConstants validationType) {
        this.validationType = validationType;
    }
}
