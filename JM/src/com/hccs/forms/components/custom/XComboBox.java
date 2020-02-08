package com.hccs.forms.components.custom;

import com.hccs.forms.components.custom.constants.ValidationConstants;
import com.hccs.forms.components.custom.util.XComponentHelper;
import com.hccs.forms.components.custom.validations.Validation;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;

/**
 * A custom combo box with built-in validation
 */
public class XComboBox extends JComboBox implements XComponent {

    private XComponentHelper helper;
    private Insets borderInsets;

    /**
     * Constructor with default values
     */
    public XComboBox() {
        this(null);
    }

    /**
     * Constructor with added information on it, an icon is added on the right side
     * of the combo box and when hovered the information will be displayed on a tooltip
     * 
     * @param info      the information to about the combo box
     */
    public XComboBox(String info) {
        borderInsets = new Insets(0, 1, 0, 1);
        helper = new XComponentHelper(this, borderInsets, info);
    }

    /**
     * Validates the component with the validations on <code>getValidations()</code>
     * this will create a tooltip with the validation messages on it and will add
     * icon and border to the component
     * 
     * @return      true if validation passes, else validation fail
     */
    @Override
    public boolean validateField() {
        return helper.validateField();
    }

    /**
     * This is the same as the <code>validateField()</code> but with extra information in it
     * 
     * @param include           if true it will only validate the given validationNames 
     *                          else it will exclude it
     * @param validationName    list of validationNames to included/excluded
     * @return                  true if validation passes, else validation fail
     */
    @Override
    public boolean validateField(boolean include, String... validationName) {
        return helper.validateField(include, validationName);
    }

    /**
     * Return this component to the Normal state, all the validation messages, icon will be remove
     */
    @Override
    public void reset() {
        helper.reset();
    }

    /**
     * Gets all the validation messages as map, with the key of <code>ValidationConstants.WARNING</code>
     * and <code>ValidationConstants.ERROR</code>
     * 
     * @return          the map of the validation messages
     */
    @Override
    public Map<ValidationConstants, List<String>> getMessages() {
        return helper.getMessages();
    }
    
    /**
     * Gets all the warning validation messages
     * 
     * @return          the list of all warning validation messages
     */
    @Override
    public List<String> getWarnings() {
        return helper.getWarnings();
    }

    /**
     * Gets all the error validation messages
     * 
     * @return          the list of all error validation messages
     */
    @Override
    public List<String> getErrors() {
        return helper.getErrors();
    }

    /**
     * Gets the information that will be displayed on the information tooltip
     * and the information icon on the right side of this combobox
     * 
     * @return      the information of the combobox
     */
    @Override
    public String getInfo() {
        return helper.getInfo();
    }

    /**
     * Sets the information that will be displayed on the information tooltip
     * and the information icon on the right side of this combobox
     * 
     * @param info   the information of the combobox
     */
    @Override
    public void setInfo(String info) {
        helper.setInfo(info);
    }

    /**
     * Gets all the validation of this combobox
     * 
     * @return      the list of <code>Validation</code> of this combobox
     */
    @Override
    public List<Validation> getValidations() {
        return helper.getValidations();
    }

    /**
     * Sets all the validation of this combobox
     * 
     * @param validations   the list of <code>Validation</code> of this combobox
     */
    @Override
    public void setValidations(List<Validation> validations) {
        helper.setValidations(validations);
    }

    /**
     * Adds a <code>Validation</code> for this combobox
     * 
     * @param validation        the validation to be added
     */
    @Override
    public void addValidation(Validation validation) {
        helper.getValidations().add(validation);
    }

    /**
     * Adds an array of <code>Validation</code> for this combobox
     * 
     * @param validations        the array of validations to be added
     */
    @Override
    public void addValidations(Validation... validations) {
        helper.getValidations().addAll(Arrays.asList(validations));
    }

    /**
     * Removes a <code>Validation</code> on the list of validations of this combobox
     * 
     * @param validation        the validation to be removed
     */
    @Override
    public void removeValidation(Validation validation) {
        helper.getValidations().remove(validation);
    }

    /**
     * Gets the current inset of this combobox
     * 
     * @return      the current inset of this combobox
     */
    public Insets getBorderInsets() {
        return borderInsets;
    }

    /**
     * Sets this combobox's border <code>Insets</code>
     * 
     * @param borderInsets      the <code>Insets</code> to be used on the border of this combobox
     */
    public void setBorderInsets(Insets borderInsets) {
        this.borderInsets = borderInsets;
        helper.setBorderInsets(borderInsets);
    }

    /**
     * Sets this combobox's border to an Error
     * 
     * @param error     The error message to show when hovered
     */
    @Override
    public void setError(String error) {
        helper.setError(error);
    }

    /**
     * Sets this combobox's border to a warning
     * 
     * @param warning     The warning message to show when hovered
     */
    @Override
    public void setWarning(String warning) {
        helper.setWarning(warning);
    }
}
