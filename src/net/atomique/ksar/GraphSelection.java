/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.jfree.data.time.TimeSeries;

/**
 *
 * @author alex
 */
public class GraphSelection {
        private static final long serialVersionUID = 4L;

        public GraphSelection(kSar hissar, Map<String,AllGraph> hispdfList, String filename, String type) {
                mysar=hissar;
                pdffilename=filename;
                Map<String,AllGraph> tmphash = new TreeMap<String,AllGraph>(hispdfList);
                toprinthash = new HashMap<String,JCheckBox>();
                graphtype = type;
                pdfList=hispdfList;
                mysar.okforprinting=false;
                boolean mustbeselected=true;
                mysar.printList.clear();
                JPanel   panel0 = new JPanel();
                JPanel   panel1 = new JPanel(new GridLayout(0,1));
                JPanel   panel3 = new JPanel(new GridLayout(5,0));
                panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                final JDialog dialog = new JDialog();
                dialog.setVisible(true);

                JButton okButton                = new JButton("Ok");
                JButton cancelButton    = new JButton("Cancel");
                JButton saButton                = new JButton("Select All");
                JButton uaButton                = new JButton("Unselect All");
                JButton woButton                = new JButton("Unselect Disks");

                String prefGraph = kSarConfig.readSpecial("PDF:" + mysar.hostName);

                for (Iterator<String> it=tmphash.keySet().iterator(); it.hasNext(); ) {
                        mustbeselected=true;
                        String key = it.next();
                        AllGraph value = tmphash.get(key);
                        if ( key.indexOf("-t2") >0 ) { continue; }
                        if ( key.indexOf("-if2") >0 ) { continue; }
                        if ( key.indexOf("Aixwait") >0 ) { continue; }
                        if ( key.indexOf("Solariswait") >0 ) { continue; }
                        if ( key.indexOf("Hpuxwait") >0 ) { continue; }
                        if ( prefGraph != null ) {
                                if ( prefGraph.indexOf(" " + key + " ") < 0 ) {
                                        mustbeselected=false;
                                }
                        }
                        JCheckBox tmp = new JCheckBox( value.getcheckBoxTitle() + " ", mustbeselected);
                        toprinthash.put(key, tmp);
                        panel1.add(tmp);
                }
                //
                JScrollPane jscroll = new JScrollPane(panel1);
                jscroll.setPreferredSize(new Dimension(300 , 200));
                jscroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                jscroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                panel3.add(okButton);
                panel3.add(cancelButton);
                panel3.add(uaButton);
                panel3.add(saButton);
                panel3.add(woButton);

                okButton.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                                dialog.setVisible(false);
                                dialog.dispose();
                                mysar.okforprinting=true;
                                parsebox();
                        }
                } );
                cancelButton.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                                dialog.setVisible(false);
                                dialog.dispose();
                                mysar.okforprinting=true;
                        }
                } );

                saButton.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                                for (Iterator<String> it=toprinthash.keySet().iterator(); it.hasNext(); ) {
                                        String key = it.next();
                                        JCheckBox value = toprinthash.get(key);
                                        value.setSelected(true);
                                }
                        }
                } );
                uaButton.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                                for (Iterator<String> it=toprinthash.keySet().iterator(); it.hasNext(); ) {
                                        String key = it.next();
                                        JCheckBox value = toprinthash.get(key);
                                        value.setSelected(false);
                                }
                        }
                } );
                woButton.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                                for (Iterator<String> it=toprinthash.keySet().iterator(); it.hasNext(); ) {
                                        String key = it.next();
                                        JCheckBox value = toprinthash.get(key);
                                        if ( key.toString().indexOf("-t1") >0 ) { value.setSelected(false); }
                                        //if ( key.toString().indexOf("-if1") >0 ) { ((JCheckBox)value).setSelected(false); }
                                        if ( key.toString().indexOf("Solarisxfer") >0 ) { value.setSelected(false); }
                                        if ( key.toString().indexOf("Hpuxxfer") >0 ) { value.setSelected(false); }
                                        if ( key.toString().indexOf("Aixxfer") >0 ) { value.setSelected(false); }

                                }
                        }
                } );

                panel0.add(jscroll);
                panel0.add(panel3);
                dialog.getContentPane().add(panel0);
                //dialog.setSize(250,80);
                dialog.pack();
                dialog.setLocationRelativeTo(mysar.myUI);
                
        }

        public void parsebox() {
                for (Iterator<String> it=toprinthash.keySet().iterator(); it.hasNext(); ) {
                        String key = it.next();
                        JCheckBox value = toprinthash.get(key);
                        if ( value.isSelected() ){
                                if ( key.indexOf("-t1") >0 ) {
                                        String diskn = key.substring(0, (key.length())-3);
                                        if ( (pdfList.get(diskn + "-t1")) != null ) {
                                                mysar.printList.put(diskn + "-t1", pdfList.get(diskn + "-t1"));
                                        }
                                        if ( (pdfList.get(diskn + "-t2")) != null ) {
                                                mysar.printList.put(diskn + "-t2", pdfList.get(diskn + "-t2"));
                                        }
                                        continue;
                                }
                                if ( key.indexOf("-if1") >0 ) {
                                        String diskn = key.substring(0, (key.length())-4);
                                        if ( (pdfList.get(diskn + "-if1")) != null ) {
                                                mysar.printList.put(diskn + "-if1", pdfList.get(diskn + "-if1"));
                                        }
                                        if ( (pdfList.get(diskn + "-if2")) != null ) {
                                                mysar.printList.put(diskn + "-if2", pdfList.get(diskn + "-if2"));
                                        }
                                        continue;
                                }
                                if ( key.indexOf("Solarisxfer") >0 ) {
                                        String diskn = key.substring(0, (key.length())-11);
                                        if ( (pdfList.get(diskn + "Solarisxfer")) != null ) {
                                                mysar.printList.put(diskn + "Solarisxfer", pdfList.get(diskn + "Solarisxfer"));
                                        }
                                        if ( (pdfList.get(diskn + "Solariswait")) != null ) {
                                                mysar.printList.put(diskn + "Solariswait", pdfList.get(diskn + "Solariswait"));
                                        }
                                        continue;
                                }
                                if ( key.indexOf("Hpuxxfer") >0 ) {
                                        String diskn = key.substring(0, (key.length())-8);
                                        if ( (pdfList.get(diskn + "Hpuxxfer")) != null ) {
                                                mysar.printList.put(diskn + "Hpuxxfer", pdfList.get(diskn + "Hpuxxfer"));
                                        }
                                        if ( (pdfList.get(diskn + "Hpuxwait")) != null ) {
                                                mysar.printList.put(diskn + "Hpuxwait", pdfList.get(diskn + "Hpuxwait"));
                                        }
                                        continue;
                                }
                                if ( key.indexOf("Aixxfer") >0 ) {
                                        String diskn = key.substring(0, (key.length())-7);
                                        if ( (pdfList.get(diskn + "Aixxfer")) != null ) {
                                                mysar.printList.put(diskn + "Aixxfer", pdfList.get(diskn + "Aixxfer"));
                                        }
                                        if ( (pdfList.get(diskn + "Aixwait")) != null ) {
                                                mysar.printList.put(diskn + "Aixwait", pdfList.get(diskn + "Aixwait"));
                                        }
                                        continue;
                                }
                                if ( (pdfList.get(key)) != null ) {
                                        mysar.printList.put(key, pdfList.get(key));
                                }
                        }
                }
                if ( ! mysar.okforprinting ) {
                        return;
                }
                if ( mysar.printList.size() < 1 ) {
                        return;
                }
                Map<String,AllGraph> tmphash = new HashMap<String,AllGraph>();
                tmphash.putAll(mysar.printList);
                for (Iterator<String> it=tmphash.keySet().iterator(); it.hasNext(); ) {
                        String key = it.next();
                        AllGraph value = tmphash.get(key);
                        hostPrintPrefs=hostPrintPrefs.concat(" " + key + " ");
                }
                
                kSarConfig.writeSpecial("PDF:" + mysar.hostName, hostPrintPrefs.toString());
                if ( "PDF".equals(graphtype) ) {
                        mysar.myUI.exportPdf(pdffilename, mysar.printList);
                }
                if ( "JPG".equals(graphtype) ) {
                        mysar.myUI.exportJpg(pdffilename, mysar.printList);
                }
                if ( "PNG".equals(graphtype) ) {
                        mysar.myUI.exportPng(pdffilename, mysar.printList);
                }
        }
        kSar mysar;
        Map<String,JCheckBox> toprinthash;
        Map<String,AllGraph> pdfList;
        String hostPrintPrefs=new String("");
        String pdffilename;
        String graphtype;

}
