package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class MainPanel extends JPanel {
    private MainPanel() {
        super(new BorderLayout());

        String[] columnNames = {"String", "Integer", "Boolean"};
        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            @Override public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        for (int i = 0; i < 1000; i++) {
            model.addRow(new Object[] {"aaaaa", i, i % 2 == 0});
        }
        JTable table = new JTable(model) {
            private transient MouseAdapter handler;
            @Override public void updateUI() {
                removeMouseMotionListener(handler);
                removeMouseListener(handler);
                super.updateUI();
                handler = new DragScrollingListener(this);
                addMouseMotionListener(handler);
                addMouseListener(handler);
            }
        };
        JScrollPane scroll = new JScrollPane(table);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        add(scroll);
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

class DragScrollingListener extends MouseAdapter {
    protected static final int VELOCITY = 5;
    protected static final int DELAY = 10;
    protected static final double GRAVITY = .95;
    protected final Cursor dc = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    protected final Cursor hc = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    protected final Timer scroller;
    protected final Point startPt = new Point();
    protected final Point delta = new Point();

    protected DragScrollingListener(JComponent c) {
        super();
        this.scroller = new Timer(DELAY, e -> {
            JViewport vport = (JViewport) SwingUtilities.getUnwrappedParent(c);
            Point vp = vport.getViewPosition();
            vp.translate(-delta.x, -delta.y);
            c.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
            if (Math.abs(delta.x) > 0 || Math.abs(delta.y) > 0) {
                delta.setLocation((int) (delta.x * GRAVITY), (int) (delta.y * GRAVITY));
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
    }
    @Override public void mousePressed(MouseEvent e) {
        Component c = e.getComponent();
        c.setCursor(hc);
        c.setEnabled(false);
        Container p = SwingUtilities.getUnwrappedParent(c);
        if (p instanceof JViewport) {
            JViewport vport = (JViewport) p;
            Point cp = SwingUtilities.convertPoint(c, e.getPoint(), vport);
            startPt.setLocation(cp);
            scroller.stop();
        }
    }
    @Override public void mouseDragged(MouseEvent e) {
        Component c = e.getComponent();
        Container p = SwingUtilities.getUnwrappedParent(c);
        if (p instanceof JViewport) {
            JViewport vport = (JViewport) p;
            Point cp = SwingUtilities.convertPoint(c, e.getPoint(), vport);
            Point vp = vport.getViewPosition();
            vp.translate(startPt.x - cp.x, startPt.y - cp.y);
            delta.setLocation(VELOCITY * (cp.x - startPt.x), VELOCITY * (cp.y - startPt.y));
            ((JComponent) c).scrollRectToVisible(new Rectangle(vp, vport.getSize()));
            startPt.setLocation(cp);
        }
    }
    @Override public void mouseReleased(MouseEvent e) {
        Component c = e.getComponent();
        c.setCursor(dc);
        c.setEnabled(true);
        scroller.start();
    }
}
