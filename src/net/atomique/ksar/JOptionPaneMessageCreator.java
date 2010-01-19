package net.atomique.ksar;

import java.awt.Component;
import java.util.Collection;
import java.util.Collections;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;


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
    public String showTextInputDialog(String title, String message) {
        return JOptionPane.showInputDialog(this.parent, message);
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IMessageCreator#showTextInputWithSuggestionDialog(java.lang.String, java.lang.String, java.lang.Iterable, java.lang.String)
     */
    @Override
    public String showTextInputWithSuggestionDialog(String title, String message, 
                                                    Collection<String> suggestions, String defaultValue) {
        /*DefaultComboBoxModel model = new DefaultComboBoxModel();
        
        if (suggestions != null) {
            for (String suggestion : suggestions) {
                model.addElement(suggestion);
            }
        }
        
        if (defaultValue != null) {
            model.setSelectedItem(defaultValue);
        }
        
        JComboBox comboBox = new JComboBox(model);
        comboBox.setEditable(true);*/
        
        Object result = JOptionPane.showInputDialog(this.parent, message, title, JOptionPane.PLAIN_MESSAGE, 
                                                    null, suggestions.toArray(), defaultValue);
        
        return ((result != null) ? result.toString() : null);
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
