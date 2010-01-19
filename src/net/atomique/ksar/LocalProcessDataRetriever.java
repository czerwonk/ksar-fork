package net.atomique.ksar;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JOptionPane;


/**
 * @author Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class LocalProcessDataRetriever implements IDataRetriever {

	private String command;
	private boolean promptForData;
	
	
	/**
	 * Creates an instance of LocalProcessDataRetriever
	 * @param command
	 * @param promptForData
	 */
    public LocalProcessDataRetriever(String command, boolean promptForData) {
		super();
		
		this.command = command;
		this.promptForData = promptForData;
	}

    
	public String getCommand () {
        return this.command;
    }
	
	@Override
	public Reader getData() throws DataRetrievingFailedException {
        if (this.command == null || this.promptForData) {   
            String suggestion = ((this.command != null) ? this.command : "sar -A");
            this.command = JOptionPane.showInputDialog("Enter local command ", suggestion);
        }
		
        if (this.command == null) {
            throw new DataRetrievingFailedException("No command specified.");
        }
        
        try {
        	String[] envvars = new String[1];
        	envvars[0] = new String("LC_ALL=C");

            Process process = Runtime.getRuntime().exec(this.command, envvars);
            
            return new InputStreamReader(process.getInputStream());
        }
        catch (IOException ex) {
        	throw new DataRetrievingFailedException("There was a problem while running the command " + this.command, ex);
        }
        catch (SecurityException ex) {
        	throw new DataRetrievingFailedException("There was a problem while running the command " + this.command, ex);
        }
	}

	@Override
	public String getRedoCommand() {
		return ("cmd://" + this.command);
	}
}