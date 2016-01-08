package gui;

import controller.HardwareMonitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Creates log gui frame read from System.out
 */
public class LogGui extends JFrame implements ActionListener {

    public boolean guiSet = false;
    private HardwareMonitor hardwareMonitor;
    JButton btn;
    JScrollPane sp;
    JPanel throttle;
    JLabel lbl;
    JTextField tf;

    @Override
    public void actionPerformed(ActionEvent e) {
        hardwareMonitor.setThrottlingValue(tf.getText());
        tf.setText("");
    }

    public LogGui(HardwareMonitor hm) {

        super("Dynamic Load Balancer Simulator");

        hardwareMonitor = hm;

        Container container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

        JTextArea ta = new JTextArea(30, 65);
        TextAreaOutputStream taos = new TextAreaOutputStream(ta);
        PrintStream ps = new PrintStream( taos );
        System.setOut(ps);
        System.setErr(ps);

        sp = new JScrollPane(ta);

        throttle = new JPanel();
        lbl = new JLabel("Throttle Value:");
        tf = new JTextField();
        btn = new JButton("Submit");
        btn.addActionListener(this);

        throttle.setLayout(new BoxLayout(throttle, BoxLayout.LINE_AXIS));

        throttle.add(lbl);
        throttle.add(Box.createRigidArea(new Dimension(10, 0)));
        throttle.add(tf);
        throttle.add(Box.createRigidArea(new Dimension(10, 0)));
        throttle.add(btn);

        container.add(sp);
        container.add(throttle);

        this.pack();
        this.setVisible(true);

        guiSet = true;
    }
}