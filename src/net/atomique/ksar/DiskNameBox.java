/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author alex
 */
public class DiskNameBox {

    public DiskNameBox(kSarUI hisUI, kSar hissar) {
        mysar = hissar;
        myUI = hisUI;
        JPanel panel0 = new JPanel();
        panel0.setLayout(new BoxLayout(panel0, BoxLayout.PAGE_AXIS));
        JPanel panel1 = new JPanel();
        JPanel panel3 = new JPanel(new GridLayout(1, 3));
        //button
        JButton OkButton = new JButton("Ok");
        JButton CancelButton = new JButton("Cancel");
        JButton ResetButton = new JButton("Reset");
        // composant

        JPanel panelname = new JPanel(new GridLayout(0, 1));
        JPanel panelvalue = new JPanel(new GridLayout(0, 1));
        JPanel panelinfo = new JPanel(new BorderLayout());
        panelinfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelinfo.add(panelname, BorderLayout.CENTER);
        panelinfo.add(panelvalue, BorderLayout.LINE_END);
        //
        JScrollPane jscroll = new JScrollPane(panelinfo);
        jscroll.setPreferredSize(new Dimension(300, 200));
        //jscroll.setBorder(BorderFactory.createLineBorder (Color.blue, 2));
        jscroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jscroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel1.add(jscroll);
        panel3.add(OkButton);
        panel3.add(CancelButton);
        panel3.add(ResetButton);
        // master panel
        panel0.add(panel1);
        panel0.add(panel3);
        // dialog

        final Frame dialog = new Frame("");
        dialog.setTitle("Disk Name");
        triggerentries(panelname, panelvalue);

        OkButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int tosave = 0;
                StringBuffer tmp = new StringBuffer("");
                for (Iterator<String> it = mysar.AlternateDiskName.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    diskName value = mysar.AlternateDiskName.get(key);
                    if (value.fieldtouch() == 1) {
                        value.setTitle(value.getfieldtext());
                        tosave = 1;
                    }
                    String myrec = value.showrecord();
                    if (myrec != null) {
                        tmp.append(myrec + "!");
                    }
                }
                if (tmp.length() > 1 && tosave == 1) {
                    kSarConfig.writeSpecial("ADISK:" + mysar.hostName, tmp.toString());
                }
                mysar.myUI.refreshGraph();
                dialog.setVisible(false);
                mysar.refreshdisktree();
                dialog.dispose();
            }
        });

        CancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        ResetButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // reset to last value
                for (Iterator<String> it = mysar.AlternateDiskName.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    diskName value = mysar.AlternateDiskName.get(key);
                    value.resetfield();
                }
            }
        });

        dialog.add(panel0);
        //dialog.setSize(250,80);
        dialog.pack();
        //dialog.setLocationRelativeTo(mysar.myUI);
        dialog.setVisible(true);
    }

    private void triggerentries(JPanel namepanel, JPanel valuepanel) {
        // CPU idle
        for (Iterator<String> it = mysar.AlternateDiskName.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            diskName value = mysar.AlternateDiskName.get(key);
            JLabel labeltmp = new JLabel((String) key + " ", JLabel.RIGHT);
            labeltmp.setLabelFor(value.getField());
            valuepanel.add(value.getField());
            namepanel.add(labeltmp);
        }
    }

    private void triggerentry(JPanel namepanel, JPanel valuepanel, String label, String value) {
        JTextField jtext = new JTextField(value, 10);
        JLabel jlabel = new JLabel(label + ": ", JLabel.RIGHT);
        jlabel.setLabelFor(jtext);
        valuepanel.add(jtext);
        namepanel.add(jlabel);
    }
    //double value = 100;
    //double valuedefault= 99;
    kSar mysar = null;
    kSarUI myUI = null;
}
