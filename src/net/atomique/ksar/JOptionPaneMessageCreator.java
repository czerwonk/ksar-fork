package net.atomique.ksar;

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;


/**
 * @author Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class JOptionPaneMessageCreator implements IMessageCreator {

    private final Component parent;
    
    
    /**
     * Creates an instance of JOptionPaneMessageCreator
     */
    public JOptionPaneMessageCreator(Component parent) {
        this.parent = parent;
    }

    
    /* (non-Javadoc)
     * @see net.atomique.ksar.IMessageCreator#showConfirmationDialog(java.lang.String, java.lang.String)
     */
    @Override
    public boolean showConfirmationDialog(String title, String message) {
        return (JOptionPane.showConfirmDialog(this.parent, message, title, JOptionPane.DEFAULT_OPTION) == JOptionPane.OK_OPTION);
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IMessageCreator#showInfoMessage(java.lang.String, java.lang.String)
     */
    @Override
    public void showInfoMessage(String title, String message) {
        JOptionPane.showMessageDialog(this.parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IMessageCreator#showTextInputDialog(java.lang.String, java.lang.String)
     */
    @Override
    public String showTextInputDialog(String title, String message, boolean maskedInput) {
        if (maskedInput) {
            JPasswordField passwordField = new JPasswordField();
            passwordField.setToolTipText(message);
            
            int result = JOptionPane.showConfirmDialog(this.parent, passwordField, title, JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                return new String(passwordField.getPassword());
            }
            
            return null;
        }
        else {
            return JOptionPane.showInputDialog(this.parent, message, title, JOptionPane.PLAIN_MESSAGE);    
        }
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IMessageCreator#showTextInputWithSuggestionDialog(java.lang.String, java.lang.String, java.lang.Iterable, java.lang.String)
     */
    @Override
    public String showTextInputWithSuggestionDialog(String title, String message, Iterable<String> suggestions, String defaultValue) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        
        if (suggestions != null) {
            for (String suggestion : suggestions) {
                model.addElement(suggestion);
            }
        }
        
        if (defaultValue != null) {
            model.setSelectedItem(defaultValue);
        }
        
        JComboBox comboBox = new JComboBox(model);
        comboBox.setEditable(true);
        comboBox.setToolTipText(message);
        
        int result = JOptionPane.showConfirmDialog(this.parent, comboBox, title, JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION 
                && comboBox.getSelectedItem() != null) {
            return comboBox.getSelectedItem().toString();
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IMessageCreator#showErrorMessage(java.lang.String, java.lang.String)
     */
    @Override
    public void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this.parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IMessageCreator#showWarningMessage(java.lang.String, java.lang.String)
     */
    @Override
    public void showWarningMessage(String title, String message) {
        JOptionPane.showMessageDialog(this.parent, message, title, JOptionPane.WARNING_MESSAGE);
    }
}
