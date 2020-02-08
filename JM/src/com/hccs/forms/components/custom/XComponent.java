package com.hccs.forms.components.custom;

import com.hccs.forms.components.custom.constants.ValidationConstants;
import com.hccs.forms.components.custom.validations.Validation;
import java.util.List;
import java.util.Map;

public interface XComponent{
    public boolean validateField();
    public boolean validateField(boolean include, String... validationName);
    public void reset();
    public Map<ValidationConstants, List<String>> getMessages();
    public List<String> getWarnings();
    public List<String> getErrors();
    public String getInfo();
    public void setInfo(String info);
    public void setError(String error);
    public void setWarning(String warning);
    public List<Validation> getValidations();
    public void setValidations(List<Validation> validations);
    public void addValidation(Validation validation);
    public void addValidations(Validation... validations);
    public void removeValidation(Validation validation);
}