package net.atomique.ksar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshDataRetriever implements IDataRetriever {

	private static Pattern pattern = Pattern.compile("^([^@]+)+@([^:]+)(?:\\:(\\d{1,5}))?$");
	
    private String host;
    private String user;
    private int port = 22;
    private String command;
    private String password;
    private final boolean promptForData;
	
	
	private SshDataRetriever(boolean promptForData) {
		this.promptForData = promptForData;
		JSch.setLogger(null);
	}
	
    /**
     * Creates an instance of SshDataRetriever
     * @param suggestedServer
     * @param suggestedCommand
     */
	public SshDataRetriever(String suggestedServer, String suggestedCommand) {
		this(true);
		
        this.setServer(suggestedServer);
        this.command = suggestedCommand;
	}
	
	/**
	 * Creates an instance of SshDataRetriever
	 * @param redoCommand
	 */
	public SshDataRetriever(String redoCommand) {
		this(false);

		this.parseRedoCommand(command);
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
        return String.format("%s%s@%s%s", 
                             ((this.user != null) ? this.user : "user.name"),
                             ((this.password != null) ? ":" + this.password : ""),
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

	@Override
	public Reader getData() throws DataRetrievingFailedException {
		Session session = connect();
		
		this.requestCommand();
        
		ChannelExec channel = null;
		
        try {
    		channel = (ChannelExec)session.openChannel("exec");
    		channel.setCommand("LC_ALL=C " + this.command + "\n");
            channel.setXForwarding(false);
            channel.setErrStream(System.err);
        	
			channel.connect();
			this.addCommandToLastUsed();
			
			return this.readToString(channel.getInputStream());
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
        	
        	if (session != null) {
        		session.disconnect();
        	}
        }
	}
	
	private Reader readToString(InputStream inputStream) throws IOException {
		Reader reader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(reader);
	
		StringWriter writer = new StringWriter();
		BufferedWriter bufferedWriter = new BufferedWriter(writer);
	
		try {
			String line = null;
			
			while ((line = bufferedReader.readLine()) != null) {
				bufferedWriter.append(line);
				bufferedWriter.newLine();
			}
			
			return new StringReader(writer.getBuffer().toString());
		}
		finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();					
				}
				catch (IOException ex) {
					// this exception would hide the exception thrown in outer try
				}
			}
			
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				}
				catch (IOException ex) {
					// this exception would hide the exception thrown in outer try
				}
			}
		}
	}

	private void requestCommand() {
		// TODO: implement me!
		throw new NotImplementedException();
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
				|| this.user == null
				|| this.password == null) {
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
			
			session.setUserInfo(null);	// TODO: user info
			this.setPasswordIfKnown(session);
			
			session.connect();
			this.addServerToLastUsedList();
			
			return session;
		} 
		catch (JSchException ex) {
			throw new DataRetrievingFailedException("Could not connect to server.", ex);
		}
	}

	private void requestConnection() {
		// TODO: implement me!
		throw new NotImplementedException();
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
		String server = this.getServer();
		
		if (!kSarConfig.sshconnectionmap.contains(server)) {
			kSarConfig.sshconnectionmap.add(server);
			kSarConfig.writeDefault();
		}
	}

	@Override
	public String getRedoCommand() {
		return ("ssh://" + this.getServer() + "/" + this.command);
	}
}