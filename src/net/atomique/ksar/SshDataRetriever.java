package net.atomique.ksar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;


/**
 * @author Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class SshDataRetriever implements IDataRetriever {

	private static Pattern pattern = Pattern.compile("^([^@]+)+@([^:]+)(?:\\:(\\d{1,5}))?$");
	private static final int MAX_READY_TIMEOUT = 10;
	private static String passphrase;
	
    private String host;
    private String user;
    private int port = 22;
    private String command;
    private String password;
    private final boolean promptForData;
	private final IMessageCreator messageCreator;
    
    
    static {
    	JSch.setLogger(new DefaultLogger());    	
    }
	
	private SshDataRetriever(boolean promptForData, IMessageCreator messageCreator) {
		this.promptForData = promptForData;
		this.messageCreator = messageCreator;
	}
	
    /**
     * Creates an instance of SshDataRetriever
     * @param suggestedServer
     * @param suggestedCommand
     */
	public SshDataRetriever(String suggestedServer, String suggestedCommand, IMessageCreator messageCreator) {
		this(true, messageCreator);
		
        this.setServer(suggestedServer);
        this.command = suggestedCommand;
	}
	
	/**
	 * Creates an instance of SshDataRetriever
	 * @param redoCommand
	 */
	public SshDataRetriever(String redoCommand, IMessageCreator messageCreator) {
		this(false, messageCreator);

		this.parseRedoCommand(redoCommand);
	}

	
    private void parseRedoCommand(String redoCommand) {
        if (redoCommand == null) {
            return;
        }
        
        String[] splitted = redoCommand.split("/");
        
        if (splitted.length != 2) {
            return;
        }
        
        this.setServer(splitted[0]);
        this.command = splitted[1];
    }
    
    public String getServer() {
    	return this.getServer(true);
    }
    
    private String getServer(boolean includePassword) {
        return String.format("%s%s@%s%s", 
                             ((this.user != null) ? this.user : "user.name"),
                             ((includePassword && this.password != null) ? ":" + this.password : ""),
                             ((this.host != null) ? this.host : "localhost"),
                             ((this.port != 22) ? ":" + Integer.toString(this.port) : ""));
    }
    
    private void setServer(String server) {
        if (server == null) {
            return;
        }
        
        Matcher matcher = pattern.matcher(server);
        
        if (matcher.find()) {
            if (matcher.group(1).contains(":")) {
                String[] splittedLogin = matcher.group(1).split(":");
                
                this.user = splittedLogin[0];
                this.password = splittedLogin[1];
            }
            else
            {
                this.user = matcher.group(1);
            }
            
            this.host = matcher.group(2);
            
            if (matcher.group(3) != null) {
                this.port = Integer.parseInt(matcher.group(3));
            }
        }
    }
    
    public String getCommand() {
        return this.command;
    }

	/* (non-Javadoc)
	 * @see net.atomique.ksar.IDataRetriever#getData()
	 */
	@Override
	public Reader getData() throws DataRetrievingFailedException {
		Session session = connect();
		
		if (this.promptForData 
		        || this.command == null) {
	        this.requestCommand();		    
		}
       
		ChannelExec channel = null;
		
        try {
    		channel = (ChannelExec)session.openChannel("exec");
    		channel.setCommand("LC_ALL=C " + this.command + "\n");
            channel.setXForwarding(false);
            channel.setErrStream(System.err);
        	
			channel.connect();
			this.addCommandToLastUsed();
			
			return this.readToMemory(channel.getInputStream());
		} 
        catch (IOException ex) {
        	throw new DataRetrievingFailedException("Could not execute command.", ex);
		} 
        catch (JSchException ex) {
        	throw new DataRetrievingFailedException("Could not execute command.", ex);
		}
        finally {
        	if (channel != null) {
        		channel.disconnect();
        	} 
        	else if (session != null) {
        		session.disconnect();
        	}
        }
	}
	
	private Reader readToMemory(InputStream inputStream) throws IOException, DataRetrievingFailedException {
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			int secondsWaited = 0;

			while (inputStream.available() < 1) {
				if (secondsWaited >= MAX_READY_TIMEOUT) 
				{
					throw new DataRetrievingFailedException("Could not read from stream.");
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// nothing to do here
				}
				
				secondsWaited++;
			}
			
			int bytesRead = 0;
			byte[] buffer = new byte[2048];
			
			while ((bytesRead = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, bytesRead);
				outputStream.flush();
			}
			
			InputStream stream = new ByteArrayInputStream(outputStream.toByteArray());
			return new InputStreamReader(stream);
		}
		finally {
		    if (inputStream != null) {
		        try {
                  inputStream.close();             
                }
                catch (IOException ex) {
                    // this exception would hide the exception thrown in outer try block
                }
		    }
		    
		    if (outputStream != null) {
                try {
                    outputStream.close();             
                }
                catch (IOException ex) {
                    // this exception would hide the exception thrown in outer try block
                }		        
		    }
		}
	}

	private void requestCommand() {
		this.command = this.messageCreator.showTextInputWithSuggestionDialog("SSH command",
		                                                                     "Please enter your command.",
		                                                                     kSarConfig.sshconnectioncmd, 
		                                                                     this.command);
	}
	
	private void addCommandToLastUsed() {
		if (this.command != null 
				&& !kSarConfig.sshconnectioncmd.contains(this.command)) {
			kSarConfig.sshconnectioncmd.add(this.command);
			kSarConfig.writeDefault();
		}
	}
	
	private Session connect() throws DataRetrievingFailedException {
		JSch jsch = new JSch();
		
		// Identity
		if (kSarConfig.sshidentity != null) {
			try {
				jsch.addIdentity(kSarConfig.sshidentity.toString());
			}
			catch (JSchException ex) {
				throw new DataRetrievingFailedException("Error while adding ssh key.", ex);
			}
		}
		
		// Connection
		if (this.promptForData 
		        || this.host == null
				|| this.user == null) {
			this.requestConnection();
		}
		
		try {
			Session session = jsch.getSession(this.user, this.host, this.port);
			
			if (!kSarConfig.ssh_stricthostchecking ) {
	            Properties sessionConfig = new Properties();
	            sessionConfig.put("StrictHostKeyChecking", "no");
	            
	            session.setConfig(sessionConfig);
	        }
			else {
				this.setKnownHostFile(jsch);
			}
			
			
			SshUserInfo userInfo = new SshUserInfo(); 
			session.setUserInfo(userInfo);
			this.setPasswordIfKnown(session);
			
			session.connect();
			this.addServerToLastUsedList();
			
			return session;
		} 
		catch (JSchException ex) {
			throw new DataRetrievingFailedException("Could not connect to server.", ex);
		}
	}

	private void requestConnection() throws DataRetrievingFailedException {
		String connection = this.messageCreator.showTextInputWithSuggestionDialog("SSSH erver connection (user@host[:port])",
		                                                                          "Please enter your connection data.",
		                                                                          kSarConfig.sshconnectionmap, 
		                                                                          this.getServer(false));
		this.setServer(connection);
	}
	
	private void setPasswordIfKnown(Session session) {
		// TODO: unified user support
        
        if (this.password != null) {
            session.setPassword(this.password);
        }
	}

	private void setKnownHostFile(JSch jsch) throws DataRetrievingFailedException {
		File sshDirectory = new File(System.getProperties().getProperty("user.home"));
		File knownHostsFile = new File(sshDirectory, "known_hosts");
		
		if (knownHostsFile.exists()) {
			try {
				jsch.setKnownHosts(knownHostsFile.toString());
			} catch (JSchException ex) {
				throw new DataRetrievingFailedException("Could not load known host file.", ex);
			}
		}
	}
	
	private void addServerToLastUsedList() {
		String server = this.getServer(false);
		
		if (!kSarConfig.sshconnectionmap.contains(server)) {
			kSarConfig.sshconnectionmap.add(server);
			kSarConfig.writeDefault();
		}
	}

	/* (non-Javadoc)
	 * @see net.atomique.ksar.IDataRetriever#getRedoCommand()
	 */
	@Override
	public String getRedoCommand() {
		return String.format("ssh://%s/%s", this.getServer(), this.command);
	}
	
	
	private static class DefaultLogger implements Logger {

		@Override
		public boolean isEnabled(int arg0) {
			return true;
		}

		@Override
		public void log(int logLevel, String message) {
			switch (logLevel) {
				case Logger.FATAL:
				case Logger.ERROR:
					System.err.println(message);
					break;
					
				default:
					System.out.println(message);
			}
		}
	}
	
	private class SshUserInfo implements UserInfo {

		private int tryCountPassword = 0;
		private int tryCountPassphrase = 0;


		@Override
		public String getPassphrase() {
			return passphrase;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public boolean promptPassphrase(String message) {
			try {
				if (passphrase != null 
						&& this.tryCountPassphrase == 0) {
					return true;
				}
				
				String enteredPassphrase = messageCreator.showTextInputDialog("Passphrase", message, true);
				
				if (enteredPassphrase != null) {
					passphrase = enteredPassphrase;
					return true;
				}
				
				return false;				
			}
			finally {
				this.tryCountPassphrase++;
			}
		}

		@Override
		public boolean promptPassword(String message) {
			try {
				if (password != null 
						&& this.tryCountPassword == 0) {
					return true;
				}
				
				String enteredPassword = messageCreator.showTextInputDialog("Password", message, true);
				
				if (enteredPassword != null) {
					password = enteredPassword;
					return true;
				}
				
				return false;
			}
			finally {
				this.tryCountPassword++;
			}
		}

		@Override
		public boolean promptYesNo(String message) {
			return messageCreator.showConfirmationDialog("Confirmation", message);
		}

		@Override
		public void showMessage(String message) {
			messageCreator.showInfoMessage("Info", message);
		}
	}
}