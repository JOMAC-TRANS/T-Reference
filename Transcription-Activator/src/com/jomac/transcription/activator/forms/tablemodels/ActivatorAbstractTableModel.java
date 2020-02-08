package com.jomac.transcription.activator.forms.tablemodels;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public abstract class ActivatorAbstractTableModel<T> extends AbstractTableModel {

    public ArrayList<T> data = new ArrayList<T>();

    public abstract Object getDataAt(int rowIndex, int columnIndex);

    public abstract String[] getColumnNames();

    public abstract Class[] getColumnTypes();

    @Override
    public String getColumnName(int column) {
        String[] columnNames = getColumnNames();
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class[] columnTypes = getColumnTypes();
        return columnTypes[columnIndex];
    }

    @Override
    public int getColumnCount() {
        return getColumnNames().length;
    }

    public T getWrapperObject(int row) {

        return row >= 0 ? (T) data.get(row) : null;
    }

    public ArrayList<T> getData() {
        return data;
    }

    @Override
    public T getValueAt(int rowIndex, int columnIndex) {
        return (T) getDataAt(rowIndex, columnIndex);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    /**
     * Adds a row to the end of the model. The new row will contain
     * <code>null</code> values unless
     * <code>rowData</code> is specified. Notification of the row being added
     * will be generated.
     *
     * @param rowData optional data of the row being added
     */
    public void removeRow(int row) {
        data.remove(row);
    }

    public void addRow(T rowData) {
        data.add(rowData);
    }

    public void addAll(List<T> rowData) {
        for (T xx : rowData) {
            data.add(xx);
        }
    }

    public void removeAll() {
        data.clear();
        fireTableDataChanged();
    }
}
