package com.hccs.util;

import com.csvreader.CsvWriter;
import com.hccs.forms.components.ZDatePicker;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Time;
import java.text.ParseException;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.swing.AbstractCellEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 * Add all Component Utilities in this class.
 *
 */
public class ComponentUtils {

    /**
     * Ensures that run.run() will execute in EDT. If the current thread is the
     * AWT Event Dispatching Thread, the run.run() will execute in the current
     * thread.
     *
     * @param run Runnable object.
     */
    public static void invokeInEDT(Runnable run, boolean wait) {
        if (SwingUtilities.isEventDispatchThread()) {
            run.run();
        } else if (wait) {
            try {
                SwingUtilities.invokeAndWait(run);
            } catch (InterruptedException iex) {
            } catch (InvocationTargetException invex) {
            }
        } else {
            SwingUtilities.invokeLater(run);
        }
    }

    public static void invokeInEDT(Runnable run) {
        invokeInEDT(run, false);
    }

    public static void invokeInEDTAndWait(Runnable run) {
        invokeInEDT(run, true);
    }

    /**
     * Checks whether the <code>comboBox</code> contains <code>element</code>
     *
     * @param comboBox JComboBox
     * @param element Object
     * @return
     */
    public static boolean comboBoxHasElement(JComboBox comboBox, Object element) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i) != null && element != null
                    && element.equals(comboBox.getItemAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears the sort on current sorted column.
     *
     * @param tbl the JXTable to clear sort.
     */
    public static void clearSort(JXTable tbl) {
        if (tbl.getSortedColumn() == null) {
            return;
        }

        tbl.setSortOrder(tbl.getSortedColumn(), SortOrder.UNSORTED);
    }

    /**
     * Creates a row sorter event in ring motion. click 1 = ascending click 2 =
     * descending click 3 = no sort.
     *
     * @param tbl the JXTable to add ring sort.
     */
    public static void createRingRowSorter(final JXTable tbl) {
        final JTableHeader header = tbl.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            int clickCount = 1;
            int oldColumn;

            @Override
            public void mouseClicked(MouseEvent e) {
                SortOrder sortOrder = null;
                int column;

                column = header.columnAtPoint(e.getPoint());

                if (oldColumn != column) {
                    clickCount = 1;
                    oldColumn = column;
                }

                switch (clickCount) {
                    case 1:
                        sortOrder = SortOrder.ASCENDING;
                        break;
                    case 2:
                        sortOrder = SortOrder.DESCENDING;
                        break;
                    case 3:
                        sortOrder = SortOrder.UNSORTED;
                        break;
                }

                tbl.setSortOrder(column, sortOrder);

                clickCount++;

                if (clickCount == 4) {
                    clickCount = 1;
                }
            }
        });
    }

    public static void createAlternateHighlighter(JXTable tbl) {
        createAlternateHighlighter(tbl, new Color(235, 235, 235));
    }

    public static void createAlternateHighlighter(JXTable tbl, Color stripeColor) {
        tbl.setHighlighters(HighlighterFactory
                .createAlternateStriping(stripeColor, Color.WHITE));
        tbl.setGridColor(Color.LIGHT_GRAY);
        tbl.setSelectionBackground(new Color(50, 100, 255));
        tbl.setSelectionForeground(Color.WHITE);
    }

    public static void exportToCsv(List<Map> list, File filename, String header, String footer) throws IOException {
        exportToCsv(list, filename, header, footer, ',', true, false);
    }

    public static void exportToCsv(JTable sourceTable, File filename, String header, String footer) throws IOException {
        exportToCsv(sourceTable, filename, header, footer, ',', true, true);
    }

    public static void exportToCsv(JTable sourceTable, File filename, String header, String footer, boolean removeHTMLTag, boolean append) throws IOException {
        exportToCsv(sourceTable, filename, header, footer, ',', true, removeHTMLTag, append);
    }

    public static void exportHTBLToCsv(JTable sourceTable, File filename, String header) throws IOException {
        exportToCsv(sourceTable, filename, header, null, ',', true, true);
    }

    private static void exportToCsv(
            Object source,
            File filename,
            String header,
            String footer,
            char delimiter,
            boolean useYesNo,
            boolean removeTags)
            throws IOException {
        exportToCsv(
                source,
                filename,
                header,
                footer,
                delimiter,
                useYesNo,
                removeTags, false);
    }

    private static void exportToCsv(
            Object source,
            File filename,
            String header,
            String footer,
            char delimiter,
            boolean useYesNo,
            boolean removeTags,
            boolean append)
            throws IOException {

        CsvWriter writer = new CsvWriter(filename.getAbsolutePath());

        if (delimiter != '\u0000') {
            writer.setDelimiter(delimiter);
        }

        if (header != null) {
            writer.write(header);
            writer.endRecord();
            writer.endRecord();
        }

        if (source instanceof List) {
            for (Object field : ((List<Map>) source).get(0).keySet()) {
                writer.write(removeTags
                        ? removeHTMLTags(field.toString(), false)
                        : field.toString());
            }
            writer.endRecord();
            for (Map rows : (List<Map>) source) {
                for (Object value : rows.values()) {
                    writeToFile(value, writer, useYesNo, removeTags, append);
                }
                writer.endRecord();
            }
        } else {
            for (int i = 0; i <= ((JTable) source).getColumnCount() - 1; i++) {
                writer.write(removeTags
                        ? removeHTMLTags(((JTable) source).getColumnName(i), false)
                        : ((JTable) source).getColumnName(i));
            }
            writer.endRecord();
            for (int yy = 0; yy < ((JTable) source).getRowCount(); yy++) {
                for (int i = 0; i <= ((JTable) source).getColumnCount() - 1; i++) {
                    writeToFile(((JTable) source).getValueAt(yy, i), writer, useYesNo, removeTags, append);
                }
                writer.endRecord();
            }
        }

        if (footer != null) {
            writer.endRecord();
            writer.writeComment(footer);
        }
        writer.flush();
        writer.close();
    }

    private static void writeToFile(
            Object value,
            CsvWriter writer,
            boolean useYesNo,
            boolean removeTags,
            boolean append)
            throws IOException {

        if (value != null) {
            if (value instanceof Boolean) {
                writer.write(
                        (!useYesNo)
                                ? value.toString()
                                : value.equals(true)
                                        ? "Yes"
                                        : "No");
            } else {
                writer.write(removeTags
                        ? removeHTMLTags(value.toString(), append)
                        : value.toString());
            }
        } else {
            writer.write(null);
        }
    }

    private static String removeHTMLTags(String value, boolean append) {
        if (value.toLowerCase().contains("<html>")) {
            value = value.replace("<html>", "");
            value = value.replace("<b>", "");
            value = value.replace("</b>", "");
            value = value.replace("</html>", "");
        }
        return append ? "\"" + value + "\"" : value;
    }

    /**
     * This key selection manager will handle selections based on multiple
     * keys.<br/> ex.<br/>
     * <code>
     *  String[] items = new String[]{"Earth", "Ear", "Ant", "Boy"};
     *  JCombobox combo = new JComboBox(items);
     *  combo.setKeySelectionManager(new MultiKeySelectionManager());
     * //or
     *  String[] items = new String[]{"Earth (a)", "Ear (b)", "Ant (c)", "Boy (d)"};
     *  JCombobox combo = new JComboBox(items);
     *  combo.setKeySelectionManager(new MultiKeySelectionManager(true));
     * </code>
     */
    public static class MultiKeySelectionManager
            implements JComboBox.KeySelectionManager {

        private long lastKeyTime = 0;
        private String pattern = "";
        private boolean searchByParenthesis = false;
        private int parBeginIndex, parEndIndex;

        public MultiKeySelectionManager() {
        }

        public MultiKeySelectionManager(boolean searchByParenthesis) {
            this.searchByParenthesis = searchByParenthesis;
        }

        @Override
        public int selectionForKey(char aKey, ComboBoxModel model) {
            int selectedIndex = -1;
            Object selected = model.getSelectedItem();
            if (selected != null) {
                for (int i = 0; i < model.getSize(); i++) {
                    if (selected.equals(model.getElementAt(i))) {
                        selectedIndex = i;
                        break;
                    }
                }
            }

            long currentTime = System.currentTimeMillis();

            if (currentTime - lastKeyTime < 200) {
                pattern += ("" + aKey).toLowerCase();
            } else {
                pattern = ("" + aKey).toLowerCase();
            }

            lastKeyTime = currentTime;

            String enclosedVal = null;
            String element = null;
            for (int i = selectedIndex + 1; i < model.getSize(); i++) {
                if (model.getElementAt(i) == null) {
                    continue;
                }

                element = model.getElementAt(i).toString().toLowerCase();
                enclosedVal = null;
                if (searchByParenthesis && (parBeginIndex = element.indexOf("(")) != -1
                        && (parEndIndex = element.indexOf(")")) != -1) {
                    enclosedVal = element.substring(parBeginIndex + 1, parEndIndex);
                }

                if (element.startsWith(pattern)
                        || (enclosedVal != null && enclosedVal.startsWith(pattern))) {
                    return i;
                }
            }

            for (int i = 0; i < selectedIndex; i++) {
                if (model.getElementAt(i) == null) {
                    continue;
                }

                element = model.getElementAt(i).toString().toLowerCase();
                enclosedVal = null;
                if (searchByParenthesis && (parBeginIndex = element.indexOf("(")) != -1
                        && (parEndIndex = element.indexOf(")")) != -1) {
                    enclosedVal = element.substring(parBeginIndex + 1, parEndIndex);
                }

                if (element.startsWith(pattern)
                        || (enclosedVal != null && enclosedVal.startsWith(pattern))) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * A table cell editor for combo box. Use this class if you want to: <br/>
     * <ol> <li> Use 'ENTER' key to stop cell editing.</li> <li> Auto popup
     * contents when editing.</li> <li> Auto popup and select content when a
     * character was pressed before editing. </li> </ol> <br/><br/> ex.<br/>
     * <code>
     * jTable1.getColumnModel().getColumn(1).setCellEditor(new SmartTableCellEditor(jComboBox1));
     * </code>
     */
    public static class SmartTableCellEditor extends AbstractCellEditor
            implements TableCellEditor {

        private JComboBox combo;
        volatile Timer timer = null;
        volatile char criteria;
        volatile boolean searchCriteria = false;

        public SmartTableCellEditor(JComboBox combo) {
            this.combo = combo;
//            this.combo.addKeyListener(new KeyAdapter() {
//                @Override
//                public void keyReleased(KeyEvent e) {
//                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                        fireEditingStopped();
//                    }
//                }
//            });

            this.combo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
            combo.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            combo.setSelectedItem(value);

            if (value != null && (combo.getSelectedItem() == null
                    || !combo.getSelectedItem().toString().equals(value.toString()))) {
                ComboBoxModel model = (ComboBoxModel) combo.getModel();
                for (int xx = 0; xx < model.getSize(); xx++) {
                    Object o = model.getElementAt(xx);
                    if (o == null) {
                        continue;
                    }
                    if (o.toString().equals(value.toString())) {
                        combo.setSelectedItem(o);
                        break;
                    }
                }
            }
            return combo;
        }

        @Override
        public Object getCellEditorValue() {
            return combo.getSelectedItem();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof KeyEvent) {
                criteria = Character.toLowerCase(((KeyEvent) anEvent).getKeyChar());
                searchCriteria = true;
            }

            if (timer != null) {
                timer.stop();
            }

            timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean success = true;
                    try {
                        combo.showPopup();
                        if (searchCriteria) {
                            int selIndex = combo.getKeySelectionManager()
                                    .selectionForKey(criteria, combo.getModel());

                            combo.setSelectedIndex(selIndex);
                            searchCriteria = false;
                        }
                        combo.requestFocus();
                    } catch (Exception ex) // at this time pop up cannot show yet.
                    {
                        success = false;
                    }

                    if (success) {
                        timer.stop();
                    }
                }
            });
            timer.setRepeats(true);
            timer.start();

            return true;
        }
    }

    public static class ZDatePickerCellEditor extends AbstractCellEditor
            implements TableCellEditor {

        private ZDatePicker dateChooser;
        volatile Timer timer = null;

        public ZDatePickerCellEditor() {
            dateChooser = new ZDatePicker();
        }

        public ZDatePickerCellEditor(String datePattern, String maskPattern, char placeholder) {
            dateChooser = new ZDatePicker(datePattern, maskPattern, placeholder);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            Date date = null;
            if (value instanceof Date) {
                date = (Date) value;
            }
            dateChooser.setDate(date);
            return dateChooser;
        }

        public void setEditorTimeZone(TimeZone timezone) {
            dateChooser.setTimezone(timezone);
        }

        @Override
        public Object getCellEditorValue() {
            return dateChooser.getDate();
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                return ((MouseEvent) e).getClickCount() == 2;
            }

            if (e instanceof KeyEvent) {
                if (timer != null) {
                    timer.stop();
                }

                timer = new Timer(100, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (dateChooser.isVisible()) {
                            dateChooser.getComponent(1).requestFocus();
                            timer.stop();
                        }
                    }
                });
                timer.setRepeats(true);
                timer.start();

                return true;
            }

            return false;
        }
    }

    /**
     * Table cell editor for java.sql.Time
     */
    public static class TimeCellEditor extends AbstractCellEditor
            implements TableCellEditor {

        private static final String DATEPATTERN = "HH:mm";
        private JTextFieldDateEditor field = null;
        private TimeZone timeZone = null;

        public TimeCellEditor() {
            initComponents();
        }

        public TimeCellEditor(TimeZone timeZone) {
            this();
            this.timeZone = timeZone;
        }

        private void initComponents() {
            field = new JTextFieldDateEditor(DATEPATTERN, "##:##", '_');
        }

        @Override
        public Object getCellEditorValue() {
            if (field.getDate() != null) {
                if (timeZone != null) {
                    DateUtilities.getFormatter(DateUtilities.FORMAT_TYPE.HH_MM_WITH_TIMEZONE).setTimeZone(timeZone);
                }

                Date date = null;
                try {
                    date = DateUtilities.parse(DateUtilities.FORMAT_TYPE.HH_MM_WITH_TIMEZONE, field.getText());
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                return new Time(date.getTime());
            }
            return null;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            field.setDate(value != null ? (Time) value : null);
            return field;
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return e instanceof MouseEvent ? ((MouseEvent) e).getClickCount() == 2 : true;
        }
    }

    public static class NonEditableSkipperKeyListener
            extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            JTable table = (JTable) e.getSource();
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();

            if (!(row < 0 || col < 0) && !table.isCellEditable(row, col)) {
                int interval = (e.getKeyCode() == KeyEvent.VK_LEFT) ? -1 : 1;

                do {
                    col = col + interval;
                    if (col > table.getColumnCount() - 1) {
                        col = 0;
                        row++;
                    } else if (col < 0) {
                        col = row > 0 ? table.getColumnCount() - 1 : -1;
                        row = row > 0 ? row - 1 : row;
                    }

                    if (row < 0 || col < 0 || row >= table.getRowCount()) {
                        break;
                    } else {
                        table.changeSelection(row, col, false, false);
                    }
                } while (!table.isCellEditable(row, col));
            }
        }
    }

    public static class RewindOnEnterKeyListener
            extends KeyAdapter {

        private JTable table;

        public RewindOnEnterKeyListener() {
        }

        public RewindOnEnterKeyListener(JTable table) {
            this.table = table;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                JTable tbl = null;
                if (e.getSource() instanceof JTable) {
                    tbl = (JTable) e.getSource();
                } else if (table != null) {
                    tbl = table;
                }

                int row = tbl.getSelectedRow();
                row++;

                if (row > tbl.getRowCount() - 1) {
                    return;
                }

                int col = -1;
                do {
                    col++;
                    tbl.changeSelection(row, col, false, false);
                } while (!tbl.isCellEditable(row, col));
            }
        }
    }

    public static class RowSelectorKeyListener
            extends KeyAdapter {

        private JTable table;
        private boolean skipRow;

        public RowSelectorKeyListener() {
        }

        public RowSelectorKeyListener(JTable table) {
            this.table = table;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            JTable tbl = null;
            if (e.getSource() instanceof JTable) {
                tbl = (JTable) e.getSource();
            } else if (table != null) {
                tbl = table;
            }

            skipRow = (e.getKeyCode() == KeyEvent.VK_RIGHT
                    && tbl.getSelectedColumn() == tbl.getColumnCount() - 1);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER || skipRow) {
                JTable tbl = null;
                if (e.getSource() instanceof JTable) {
                    tbl = (JTable) e.getSource();
                } else if (table != null) {
                    tbl = table;
                }

                int row = tbl.getSelectedRow();
                if (row == -1) {
                    return;
                }
                if (tbl.getValueAt(row, tbl.getSelectedColumn()) != null || skipRow) {
                    row++;
                }

                if (row > tbl.getRowCount() - 1) {
                    return;
                }

                int col = -1;

                do {
                    col++;
                    tbl.changeSelection(row, col, false, false);
                } while (!tbl.isCellEditable(row, col));

                skipRow = false;
            }
        }
    }
}
