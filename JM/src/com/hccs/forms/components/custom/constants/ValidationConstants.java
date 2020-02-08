package com.hccs.forms.components.custom.constants;

public enum ValidationConstants {
    NORMAL,
    INFO,
    WARNING,
    ERROR;
    
    private final int val;
    private ValidationConstants(){
        this.val = ordinal();
    }
    public int getValue(){
        return val;
    }
}