package net.atomique.ksar;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JFileChooser;


/**
 * @author Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class FileSystemDataRetriever implements IDataRetriever {

	private File sarFile;
	private final boolean promptForData;
	
	
	/**
	 * Creates an instance of FileSystemDataRetriever
	 * @param file
	 * @param promptForData
	 */
    public FileSystemDataRetriever(File file, boolean promptForData) {
    	this.promptForData = promptForData;
    	this.sarFile = file;
    }
    
    
    private File getLocalFile(File file) {
        JFileChooser fc = new JFileChooser();
        
        if (file != null && file.exists()) {
            fc.setSelectedFile(file);
        }
        
        if (fc.showDialog(null, "Open") == JFileChooser.APPROVE_OPTION
                && fc.getSelectedFile().exists()) {
            return fc.getSelectedFile();
        }
        
        return null;
    }
    
    public File getSarFile() {
        return this.sarFile;
    }
	
	/* (non-Javadoc)
	 * @see net.atomique.ksar.IDataRetriever#getData()
	 */
	@Override
	public Reader getData() throws DataRetrievingFailedException {
        if (this.sarFile == null || promptForData) {
            this.sarFile = this.getLocalFile(this.sarFile);
        }
		
		if (this.sarFile == null) {
            throw new DataRetrievingFailedException("No import file specified.");
        }
		
		try {
			return new FileReader(this.sarFile);
		}
		catch (IOException ex) {
			throw new DataRetrievingFailedException(ex);
		}
	}

	/* (non-Javadoc)
	 * @see net.atomique.ksar.IDataRetriever#getRedoCommand()
	 */
	@Override
	public String getRedoCommand() {
		return ("file://" + this.sarFile);
	}
}