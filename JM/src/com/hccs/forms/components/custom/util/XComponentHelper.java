package com.hccs.forms.components.custom.util;

import com.hccs.forms.components.custom.constants.BalloonStyleConstants;
import com.hccs.forms.components.custom.constants.ValidationConstants;
import com.hccs.forms.components.custom.validations.Validation;
import com.hccs.util.ComponentUtils;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.utils.ToolTipUtils;

/**
 * Helper class for custom textbox and combobox
 */
public class XComponentHelper {

    private final JComponent component;
    private List<Validation> validations;
    private List<String> warnings, errors;
    private BalloonTip tooltip;
    private String info;
    private ValidationConstants currentType;
    private Insets borderInsets;

    public XComponentHelper(JComponent component) {
        this(component, null, null);
    }

    public XComponentHelper(JComponent component, String info) {
        this(component, null, info);
    }

    public XComponentHelper(JComponent component, Insets borderInsets) {
        this(component, borderInsets, null);
    }

    /**
     * @param component the component to be bind by this helper
     * @param borderInsets the borderInsets of the component
     * @param info the information about the component, this can be null if you
     * dont want to have an information tooltip on the component
     */
    public XComponentHelper(JComponent component, Insets borderInsets, final String info) {
        this.component = component;
        this.info = info;
        validations = new ArrayList<>();
        warnings = new ArrayList<>();
        errors = new ArrayList<>();
        currentType = info != null ? ValidationConstants.INFO : ValidationConstants.NORMAL;
        this.borderInsets = borderInsets == null ? new Insets(1, 1, 1, 1) : borderInsets;
        component.setBorder(XBorderFactory.getBorder(currentType, borderInsets));
        component.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (info != null) {
                    setupInfoTooltip();
                }
            }
        });
    }

    /**
     * Validates the component with all the validation added on validation list
     *
     * @return true if validation passes, else validation fail
     */
    public boolean validateField() {
        return validateField(false, new String[]{});
    }

    public boolean validateField(boolean include, String... validationName) {
        if (!validations.isEmpty()) {
            ValidationConstants tmpBorder = info != null
                    ? ValidationConstants.INFO : ValidationConstants.NORMAL;
            StringBuilder warningMessages = new StringBuilder();
            StringBuilder errorMessages = new StringBuilder();
            warnings.clear();
            errors.clear();
            List<String> validationNames = Arrays.asList(validationName);
            for (Validation validation : validations) {
                if ((include && validationNames.contains(validation.getName()))
                        || !(include | validationNames.contains(validation.getName()))) {
                    String message = validation.validate();
                    if (message != null) {
                        switch (validation.getValidationType()) {
                            case WARNING:
                                if (tmpBorder.getValue() <= ValidationConstants.WARNING.getValue()) {
                                    tmpBorder = ValidationConstants.WARNING;
                                    warnings.add(message);
                                    appendMessageToStringBuilder(warningMessages, message);
                                }
                                break;
                            case ERROR:
                                if (tmpBorder.getValue() <= ValidationConstants.ERROR.getValue()) {
                                    tmpBorder = ValidationConstants.ERROR;
                                    errors.add(message);
                                    appendMessageToStringBuilder(errorMessages, message);
                                }
                        }
                    }
                }
            }

            if (tmpBorder != currentType) {
                currentType = tmpBorder;
                setBorder(currentType);
            }

            StringBuilder messages = errorMessages.length() > 0
                    ? errorMessages
                    : warningMessages;

            if (messages.length() > 0) {
                messages.insert(0, "<html><ul>").append("</ul></html>");
                addToolTip(messages.toString());
                return currentType.getValue() < ValidationConstants.ERROR.getValue();
            }
            if (info != null) {
                addToolTip(info);
            } else {
                if (tooltip != null) {
                    tooltip.closeBalloon();
                }
                tooltip = null;
            }
        }

        return true;
    }

    private void appendMessageToStringBuilder(StringBuilder builder, String message) {
        builder.append("<li>")
                .append(message)
                .append("</li>");
    }

    /**
     * Return the component to the Normal state, all the validation messages,
     * icon will be remove
     */
    public void reset() {
        warnings.clear();
        errors.clear();
        if (info != null) {
            setupInfoTooltip();
        } else {
            currentType = ValidationConstants.NORMAL;
            if (tooltip != null) {
                tooltip.closeBalloon();
                tooltip = null;
            }
        }
        setBorder(currentType);
    }

    /**
     * Sets the border of the component
     *
     * @param type the type of border to be used, ValidationConstants.ERROR or
     * ValidationConstants.WARNING
     */
    public void setBorder(final ValidationConstants type) {
        ComponentUtils.invokeInEDT(new Runnable() {
            @Override
            public void run() {
                component.setBorder(XBorderFactory.getBorder(type, borderInsets));
            }
        });
    }

    /**
     * Adds tooltip to the component
     *
     * @param msg the messages on the tooltip
     */
    public void addToolTip(final String msg) {
        ComponentUtils.invokeInEDT(new Runnable() {
            @Override
            public void run() {
                if (tooltip == null) {
                    tooltip = new BalloonTip(
                            component,
                            new JLabel(),
                            BalloonStyleConstants.EDGE.getStyle(),
                            BalloonTip.Orientation.RIGHT_ABOVE,
                            BalloonTip.AttachLocation.ALIGNED,
                            20,
                            8,
                            false);
                }
                tooltip.setContents(new JLabel(msg));
                ToolTipUtils.balloonToToolTip(tooltip, 500, 5000);
            }
        });
    }

    /**
     * Adds an information tooltip to the component
     */
    private void setupInfoTooltip() {
        currentType = ValidationConstants.INFO;
        addToolTip(info);
    }

    private void setCustomValidationType(ValidationConstants type, String message) {
        if (ValidationConstants.WARNING.equals(type)) {
            warnings.add(message);
        } else {
            errors.add(message);
        }
        currentType = type;
        setBorder(type);
        addToolTip("<html><ul><li>" + message + "</li></ul></html>");
    }

    public void setWarning(String message) {
        setCustomValidationType(ValidationConstants.WARNING, message);
    }

    public void setError(String message) {
        setCustomValidationType(ValidationConstants.ERROR, message);
    }

    public Map<ValidationConstants, List<String>> getMessages() {
        Map<ValidationConstants, List<String>> messages = new HashMap<>();
        messages.put(ValidationConstants.WARNING, getWarnings());
        messages.put(ValidationConstants.ERROR, getErrors());
        return messages;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<String> getErrors() {
        return errors;
    }

    public BalloonTip getTooltip() {
        return tooltip;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
        setupInfoTooltip();
        setBorder(currentType);
    }

    public void setBorderInsets(Insets borderInsets) {
        this.borderInsets = borderInsets;
        setBorder(currentType);
    }

    public List<Validation> getValidations() {
        return validations;
    }

    public void setValidations(List<Validation> validations) {
        this.validations = validations;
    }

    public String getCurrentType() {
        return String.valueOf(currentType.getValue());
    }
}
