package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public final class MainPanel extends JPanel {
    private final RowDataModel model = new RowDataModel();
    private final JTable table = new JTable(model) {
        private final Color evenColor = new Color(250, 250, 250);
        @Override public Component prepareRenderer(TableCellRenderer tcr, int row, int column) {
            Component c = super.prepareRenderer(tcr, row, column);
            if (isRowSelected(row)) {
                c.setForeground(getSelectionForeground());
                c.setBackground(getSelectionBackground());
            } else {
                c.setForeground(getForeground());
                c.setBackground(row % 2 == 0 ? evenColor : getBackground());
            }
            return c;
        }
    };
    private final JCheckBox cbox = new JCheckBox("setEnabledAt(2, false)");

    public MainPanel() {
        super(new BorderLayout());
        JTableHeader header = table.getTableHeader();

        final SortButtonRenderer hrenderer = new SortButtonRenderer(header);
        hrenderer.setEnabledAt(0, false);

        header.setDefaultRenderer(hrenderer);
        header.addMouseListener(new HeaderMouseListener());
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        TableColumn col = table.getColumnModel().getColumn(0);
        col.setMinWidth(80);
        col.setMaxWidth(80);

        model.addRowData(new RowData("Name 1", "comment..."));
        model.addRowData(new RowData("Name 2", "Test"));
        model.addRowData(new RowData("Name d", "ee"));
        model.addRowData(new RowData("Name c", "Test cc"));
        model.addRowData(new RowData("Name b", "Test bb"));
        model.addRowData(new RowData("Name a", "ff"));
        model.addRowData(new RowData("Name 0", "Test aa"));

        cbox.addItemListener(e -> {
            hrenderer.setEnabledAt(2, !((JCheckBox) e.getItemSelectable()).isSelected());
            //if (e.getStateChange() == ItemEvent.SELECTED) {
            //    hrenderer.setEnabledAt(2, false);
            //} else if (e.getStateChange() == ItemEvent.DESELECTED) {
            //    hrenderer.setEnabledAt(2, true);
            //}
        });

        add(new JScrollPane(table));
        add(cbox, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(320, 240));
    }

    public static void main(String... args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class RowDataModel extends SortableTableModel {
    private static final ColumnContext[] COLUMN_ARRAY = {
        new ColumnContext("No.",     Integer.class, false),
        new ColumnContext("Name",    String.class,  true),
        new ColumnContext("Comment", String.class,  true)
    };
    private int number;
    public void addRowData(RowData t) {
        Object[] obj = {number, t.getName(), t.getComment()};
        super.addRow(obj);
        number++;
    }
    @Override public boolean isCellEditable(int row, int col) {
        return COLUMN_ARRAY[col].isEditable;
    }
    @Override public Class<?> getColumnClass(int column) {
        return COLUMN_ARRAY[column].columnClass;
    }
    @Override public int getColumnCount() {
        return COLUMN_ARRAY.length;
    }
    @Override public String getColumnName(int column) {
        return COLUMN_ARRAY[column].columnName;
    }
    private static class ColumnContext {
        public final String  columnName;
        public final Class   columnClass;
        public final boolean isEditable;
        protected ColumnContext(String columnName, Class columnClass, boolean isEditable) {
            this.columnName = columnName;
            this.columnClass = columnClass;
            this.isEditable = isEditable;
        }
    }
}

class RowData {
    private String name;
    private String comment;
    protected RowData(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }
    public void setName(String str) {
        name = str;
    }
    public void setComment(String str) {
        comment = str;
    }
    public String getName() {
        return name;
    }
    public String getComment() {
        return comment;
    }
}
