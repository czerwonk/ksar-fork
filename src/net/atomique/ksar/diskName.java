/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import javax.swing.JTextField;

/**
 *
 * @author alex
 */
public class diskName {

    public diskName(final String val) {
        myfield = new JTextField();
        myfield.setColumns(25);
        myfield.setText(val);
        name = val;
    }

    public JTextField getField() {
        return myfield;
    }

    public String getName() {
        return name;
    }

    public String getfieldtext() {
        return myfield.getText();
    }

    public void setTitle(final String val) {
        if (val != null) {
            userTitle = val;
            myfield.setText(val);
        }
    }

    public String getTitle() {
        if (userTitle != null) {
            return userTitle;
        } else {
            return name;
        }
    }

    public String showTitle() {
        if (userTitle != null) {
            return userTitle + " (" + name + ")";
        } else {
            return name;
        }
    }

    public String showrecord() {
        if (userTitle != null) {
            return name + "=" + userTitle;
        } else {
            return null;
        }
    }

    public int fieldtouch() {
        if (myfield.getText().equals(getTitle())) {
            return 0;
        } else {
            return 1;
        }
    }

    public void resetfield() {
        myfield.setText(getTitle());
    }
    final private JTextField myfield;
    final private String name;
    private String userTitle;
}
