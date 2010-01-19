package net.atomique.ksar;


/**
 * @author Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public interface IMessageCreator {

	void showInfoMessage(String title, String message);
	
	boolean showConfirmationDialog(String title, String message);
	
	String showTextInputWithSuggestionDialog(String title, String message, Iterable<String> suggestions);
	
	String showTextInputDialog(String title, String message);
}
