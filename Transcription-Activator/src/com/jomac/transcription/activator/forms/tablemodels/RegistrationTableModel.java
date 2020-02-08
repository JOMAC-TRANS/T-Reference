package com.jomac.transcription.activator.forms.tablemodels;

import com.hccs.util.DateUtilities;
import com.jomac.transcription.activator.jpa.models.RegistrationBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RegistrationTableModel extends ActivatorAbstractTableModel<RegistrationBean> {

    public void addBeansToModel(List<RegistrationBean> regs) {

        for (RegistrationBean reg : regs) {
            data.add(reg);
        }
    }

    public void addBeanlsToModelOrder(List<RegistrationBean> regs) {
        for (int x = (regs.size() - 1); x >= 0; x--) {
            data.add(regs.get(x));
        }
    }

    @Override
    public Object getDataAt(int rowIndex, int columnIndex) {
        if (data != null && data.size() > 0 && (rowIndex >= 0 && rowIndex <= data.size() - 1)) {
            RegistrationBean reg = data.get(rowIndex);
            Date date;

            if (reg != null) {
                switch (columnIndex) {
                    case 0:
                        try {
                            date = reg.getRegistrationdate();
                        } catch (Exception ex) {
                            date = new Date();
                        }
                        return (new SimpleDateFormat(DateUtilities.FORMAT_TYPE.MM_DD_YYYY_DASH.toString())).format(date);
                    case 1:
                        String name = reg.getPersonid().getName();
                        if (reg.getActive() != null && !reg.getActive()) {
                            return name.concat("  **DENIED**");
                        }
                        return name;
                    case 2:
                        return reg.getPersonid().getPhonenumber();
                    case 3:
                        return reg.getPersonid().getEmail();
                    case 4:
                        String prodName = reg.getProductid().getName();
                        return prodName + " " + reg.getProductid().getVersion();
                    case 5:
                        try {
                            date = reg.getLastlogin();
                            if (date == null) {
                                return "";
                            }
                        } catch (Exception ex) {
                            return "";
                        }

                        return (new SimpleDateFormat(DateUtilities.FORMAT_TYPE.MM_DD_YYYY_DASH.toString())).format(date);
                }
            }
        }
        return null;
    }

    private String getBold(String value) {
        return "<html><b>" + value + "</b></html>";
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{
            getBold("Date"),
            getBold("Name"),
            getBold("Phone Number"),
            getBold("Email Address"),
            getBold("Product"),
            getBold("Last Login")
        };
    }

    @Override
    public Class[] getColumnTypes() {
        return new Class[]{
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class
        };
    }
}
