package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

public final class MainPanel extends JPanel {
    private MainPanel() {
        super(new GridLayout(2, 1));
        SimpleDateFormat format = new SimpleDateFormat("mm:ss, SSS", Locale.getDefault());
        DefaultFormatterFactory factory = new DefaultFormatterFactory(new DateFormatter(format));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        Date d = calendar.getTime();

        JSpinner spinner1 = new JSpinner(new SpinnerDateModel(d, null, null, Calendar.SECOND));
        ((JSpinner.DefaultEditor) spinner1.getEditor()).getTextField().setFormatterFactory(factory);

        HashMap<Integer, Integer> stepSizeMap = new HashMap<>();
        stepSizeMap.put(Calendar.HOUR_OF_DAY, 1);
        stepSizeMap.put(Calendar.MINUTE,      1);
        stepSizeMap.put(Calendar.SECOND,      30);
        stepSizeMap.put(Calendar.MILLISECOND, 500);

        JSpinner spinner2 = new JSpinner(new SpinnerDateModel(d, null, null, Calendar.SECOND) {
            @Override public Object getPreviousValue() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(getDate());
                Integer calendarField = getCalendarField();
                Integer stepSize = Optional.ofNullable(stepSizeMap.get(calendarField)).orElse(1);
                cal.add(calendarField, -stepSize);
                // Date prev = cal.getTime();
                // Comparable start = getStart();
                // return ((start == null) || (start.compareTo(prev) <= 0)) ? prev : null;
                // return prev;
                return cal.getTime();
            }
            @Override public Object getNextValue() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(getDate());
                Integer calendarField = getCalendarField();
                Integer stepSize = Optional.ofNullable(stepSizeMap.get(calendarField)).orElse(1);
                cal.add(calendarField, stepSize);
                // Date next = cal.getTime();
                // Comparable end = getEnd();
                // return ((end == null) || (end.compareTo(next) >= 0)) ? next : null;
                // return next;
                return cal.getTime();
            }
        });
        ((JSpinner.DefaultEditor) spinner2.getEditor()).getTextField().setFormatterFactory(factory);

        add(makeTitlePanel("Default SpinnerDateModel", spinner1));
        add(makeTitlePanel("Override SpinnerDateModel#getNextValue(...)", spinner2));
        setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        setPreferredSize(new Dimension(320, 240));
    }
    private static Component makeTitlePanel(String title, Component cmp) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1d;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        p.add(cmp, c);
        return p;
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
