package net.atomique.ksar;

import java.io.File;


/**
 * @author Daniel Czerwonk
 *
 */
final class kSarInstanceConfig {
    
    private String lastCommand;
    private File lastFile;
    private String lastSshServer;
    
    public String getLastCommand() {
        return this.lastCommand;
    }
    
    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }
    
    public File getLastFile() {
        return this.lastFile;
    }
    
    public void setLastFile(File file) {
        this.lastFile = file;
    }
    
    public String getLastSshServer() {
        return this.lastSshServer;
    }
    
    public void setLastSshServer(String lastSshServer) {
        this.lastSshServer = lastSshServer;
    }
}
