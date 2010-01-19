package net.atomique.ksar;


/**
 * @author Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public interface IMessageCreator {

	/**
	 * Shows a dialog displaying an info text
	 * @param title Title to show
	 * @param message Message to show
	 */
	void showInfoMessage(String title, String message);
	
	/**
     * Shows a dialog displaying an error message
     * @param title Title to show
     * @param message Message to show
     */
    void showErrorMessage(String title, String message);
	
    /**
     * Shows a dialog displaying a warning message
     * @param title Title to show
     * @param message Message to show
     */
    void showWarningMessage(String title, String message);    
    
	/**
	 * Shows a dialog to confirm a request
	 * @param title Title to show
	 * @param message Message to show
	 * @return true (yes) oder false (no)
	 */
	boolean showConfirmationDialog(String title, String message);
	
	/**
	 * Shows a dialog with a text input field in a suggestion mode
	 * @param title Title to show
	 * @param suggestions Suggestions shown in the dialog
	 * @param defaultValue Value selected by default
	 * @return Entered text or null (if dialog was canceled)
	 */
	String showTextInputWithSuggestionDialog(String title, Iterable<String> suggestions, String defaultValue);
	
	/**
	 * Shows a dialog with a text input field
	 * @param title Title to show
	 * @param message Message to show
	 * @return Entered text or null (if dialog was canceled)
	 */
	String showTextInputDialog(String title, String message);
}
