package com.hccs.forms.components;

import com.hccs.forms.components.custom.XComponent;
import com.hccs.forms.components.custom.constants.ValidationConstants;
import com.hccs.forms.components.custom.util.XComponentHelper;
import com.hccs.forms.components.custom.validations.Validation;
import com.hccs.util.DateUtilities;
import com.toedter.calendar.DateUtil;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.jdesktop.swingx.JXHyperlink;

/**
 * A custom date picker with time zone.
 */
public class ZDatePicker extends JDateChooser implements XComponent {

    private XComponentHelper helper;
    private Insets borderInsets;
    private TimeZone timezone;
    private final Color FORECOLOR = new Color(0, 90, 164),
            BACKGROUND = new Color(210, 228, 238);
    private final SimpleDateFormat formatter;

    public ZDatePicker() {
        this("MM-dd-yyyy", "##-##-####", '_');
    }

    public ZDatePicker(String datePattern, String maskPattern, char placeholder) {
        super(datePattern, maskPattern, placeholder);
        initComponents();

        formatter = new SimpleDateFormat(getDateFormatString());
        formatter.setLenient(false);
    }

    // <editor-fold defaultstate="collapsed" desc="Component Initialization">
    private void initComponents() {
        JPanel toolpanel = new JPanel();
        JXHyperlink none = new JXHyperlink(),
                today = new JXHyperlink();

        none.setForeground(FORECOLOR);
        none.setClickedColor(FORECOLOR);
        today.setForeground(FORECOLOR);
        today.setClickedColor(FORECOLOR);

        none.setText("None");
        today.setText("Today: " + DateUtilities.format(DateUtilities.FORMAT_TYPE.MM_DD_YYYY_DASH, new Date()));
        toolpanel.setLayout(new BorderLayout(5, 0));
        toolpanel.setPreferredSize(new Dimension(200, 20));
        toolpanel.setBackground(BACKGROUND);
        toolpanel.setBorder(new EmptyBorder(1, 3, 1, 3));
        toolpanel.add(none, BorderLayout.EAST);
        toolpanel.add(today, BorderLayout.WEST);

        today.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() > 0) {
                    Calendar calendar = Calendar.getInstance();
                    jcalendar.setCalendar(calendar);
                }
            }
        });

        none.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() > 0) {
                    Calendar calendar = Calendar.getInstance();

                    jcalendar.setCalendar(calendar);
                    setDate(null);
                }
            }
        });
        popup.add(toolpanel);

        getComponent(1).addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_TAB) {
                    autoYearComplete();
                }
            }
        });

        getComponent(1).addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                autoYearComplete();
            }
        });

        JComponent editor = (JComponent) getComponent(1);
        borderInsets = new Insets(2, 4, 2, 2);
        helper = new XComponentHelper(editor, borderInsets, null);
        editor.setToolTipText(null);
    }
    // </editor-fold>

    public TimeZone getTimezone() {
        return timezone;
    }

    private void autoYearComplete() {
        if (getDate() != null) {
            Calendar now = Calendar.getInstance(),
                    clone = (Calendar) getCalendar().clone();

            int year = getCalendar().get(Calendar.YEAR),
                    xxx = now.get(Calendar.YEAR) % 1000;

            if (year <= xxx && year >= 0) {
                clone.roll(Calendar.YEAR, 2000);
            } else if (year >= xxx && year <= 99) {
                clone.roll(Calendar.YEAR, 1900);
            }

            setCalendar(clone);
            setDate(getCalendar().getTime());
        } else {
            String text = ((JTextFieldDateEditor) getComponent(1)).getText();
            int millenium = (Calendar.getInstance().get(Calendar.YEAR) / 100) * 100;
            if (!(text.contains("__-") || text.contains("00-"))
                    && (text.endsWith("0___") || text.endsWith("00__")
                    || text.endsWith("000_") || text.endsWith("0000"))) {

                try {
                    text = text.replace(text.substring(text.lastIndexOf("-") + 1, text.length()), String.valueOf(millenium));
                    Date date = formatter.parse(text);
                    setDate(date);
                } catch (ParseException ex) {
                }
            }
        }
    }

    public void setTimezone(TimeZone timezone) {
        this.timezone = timezone;
    }

    public Date getDateWithMinTime() {
        return DateUtilities.setMinTime(getDate());
    }

    public Date getDateWithMaxTime() {
        return DateUtilities.setMaxTime(getDate());
    }

    @Deprecated
    public Date getZonedDate() {
        Date date = getDateWithMinTime();

        if (date == null || timezone == null) {
            return null;
        }

        return DateUtilities.fixDateForTimeZone(date, timezone);
    }

    @Deprecated
    public void setZonedDate(Date date) {
        setDate(date != null ? DateUtilities.fixDateForTimeZone(date, timezone, true) : null);
    }

    public Date getDateForGMT() {
        Date date = getDate();

        if (date == null) {
            return null;
        }

        return DateUtilities.convertForGMT(date);
    }

    public void setDateToGMT(Date date) {
        setDate(date != null ? DateUtilities.convertToTimeZone(date, timezone) : null);
    }

    public boolean checkDateFormat() {
        String blank = "__-__-____";
        String text = ((JTextFieldDateEditor) getComponent(1)).getText();
        boolean result = true;

        if (text != null && text.equals(blank)) {
            return result;
        }

        try {
            Date date = formatter.parse(text);
            DateUtil du = new DateUtil();
            result = du.checkDate(date);
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    @Override
    public boolean validateField() {
        return helper.validateField();
    }

    @Override
    public boolean validateField(boolean include, String... validationName) {
        return helper.validateField(include, validationName);
    }

    @Override
    public void reset() {
        helper.reset();
    }

    @Override
    public Map<ValidationConstants, List<String>> getMessages() {
        return helper.getMessages();
    }

    @Override
    public List<String> getWarnings() {
        return helper.getWarnings();
    }

    @Override
    public List<String> getErrors() {
        return helper.getErrors();
    }

    @Override
    public String getInfo() {
        return helper.getInfo();
    }

    @Override
    public void setInfo(String info) {
        helper.setInfo(info);
    }

    @Override
    public List<Validation> getValidations() {
        return helper.getValidations();
    }

    @Override
    public void setValidations(List<Validation> validations) {
        helper.setValidations(validations);
    }

    @Override
    public void addValidation(Validation validation) {
        helper.getValidations().add(validation);
    }

    @Override
    public void addValidations(Validation... validations) {
        helper.getValidations().addAll(Arrays.asList(validations));
    }

    @Override
    public void removeValidation(Validation validation) {
        helper.getValidations().remove(validation);
    }

    public Insets getBorderInsets() {
        return borderInsets;
    }

    public void setBorderInsets(Insets borderInsets) {
        this.borderInsets = borderInsets;
        helper.setBorderInsets(borderInsets);
    }

    /**
     * Sets this zdatepicker's border to an Error
     *
     * @param error The error message to show when hovered
     */
    @Override
    public void setError(String error) {
        helper.setError(error);
    }

    /**
     * Sets this zdatepicker's border to a warning
     *
     * @param warning The warning message to show when hovered
     */
    @Override
    public void setWarning(String warning) {
        helper.setWarning(warning);
    }
}
