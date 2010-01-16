/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */ 
package net.atomique.ksar;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author alex
 */
public class AboutBox extends JInternalFrame implements ActionListener {

    public static final long serialVersionUID = 501L;
    
    private static AboutBox instance = new AboutBox();

    AboutBox() {
        super("About");
        setResizable(false);
        panel1.setLayout(borderLayout1);
        panel2.setLayout(borderLayout2);
        gridLayout1.setRows(6);
        gridLayout1.setColumns(1);
        label1.setText("kSar " + VersionNumber.getVersionNumber());
        label2.setText("Author: xavier cherif");
        label3.setText("Copyright (c) 2006 ");
        label4.setText("ARS LONGA, VITA BREVIS");
        label5.setText("Licence: BSD");
        JEditorPane copyright = new JEditorPane("text/html", "<html>" + "<span style='font-size: 10px;'>" + "<a href='http://ksar.atomique.net/'>http://ksar.atomique.net/</a>" + "</span>" + "</html>");
        copyright.setEditable(false);
        insetsPanel2.setLayout(gridLayout1);
        insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
        button1.setText("Ok");
        button1.addActionListener(this);
        panel1.add(insetsPanel1, BorderLayout.SOUTH);
        insetsPanel1.add(button1, null);
        panel2.add(insetsPanel2, BorderLayout.CENTER);
        insetsPanel2.add(label1, null);
        insetsPanel2.add(label2, null);
        insetsPanel2.add(label3, null);
        insetsPanel2.add(label4, null);
        insetsPanel2.add(label5, null);
        insetsPanel2.add(copyright, null);
        getContentPane().add(panel1, null);
        panel1.add(panel2, BorderLayout.NORTH);
        validate();
        pack();
    }
    
    final private JPanel panel1 = new JPanel();
    final private JPanel panel2 = new JPanel();
    final private JPanel insetsPanel1 = new JPanel();
    final private JPanel insetsPanel2 = new JPanel();
    final private JButton button1 = new JButton();
    final private JLabel label1 = new JLabel();
    final private JLabel label2 = new JLabel();
    final private JLabel label3 = new JLabel();
    final private JLabel label4 = new JLabel();
    final private JLabel label5 = new JLabel();
    final private BorderLayout borderLayout1 = new BorderLayout();
    final private BorderLayout borderLayout2 = new BorderLayout();
    final private GridLayout gridLayout1 = new GridLayout();

    /**Component initialization*/
    public void actionPerformed(final ActionEvent evt) {
        dispose();
    }

    public static AboutBox getInstance() {
        return instance;
    }
}
