package com.jomac.transcription.reference.forms.tablemodels;

import com.jomac.transcription.reference.jpa.models.DictatorBean;
import com.jomac.transcription.reference.jpa.models.DocumentBean;

public class ReferenceTableModel extends ReferenceAbstractTableModel<DocumentBean> {

    @Override
    public Object getDataAt(int rowIndex, int columnIndex) {
        if (data != null && data.size() > 0 && (rowIndex >= 0 && rowIndex <= data.size() - 1)) {
            DocumentBean document = data.get(rowIndex);

            if (document != null) {
                switch (columnIndex) {
                    case 0:
                        return document.getDictatorworktypeid().getDictatorid();
                    case 1:
                        return Integer.parseInt(
                                document.getDictatorworktypeid().getWorktypeid().toString());
                    case 2:
                        return document.getSearchCount();
                }
            }
        }
        return null;
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{
            "Dictator",
            "W.T.",
            "Results"
        };
    }

    @Override
    public Class[] getColumnTypes() {
        return new Class[]{
            DictatorBean.class,
            Integer.class,
            Integer.class
        };
    }
}
