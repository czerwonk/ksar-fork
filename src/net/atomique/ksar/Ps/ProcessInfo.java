/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar.Ps;

/**
 *
 * @author alex
 */
public class ProcessInfo {

    public ProcessInfo(final String hispid,final String cmd) {
        int len=50;
        StringBuffer tmp2 = new StringBuffer();
        for (int i=0; i< cmd.length(); i=i+50) {
            if ( cmd.length()-i >=50) {
                len=50;
            } else {
                len = cmd.length()-i;
            }
            tmp2.append(cmd.substring(i,i+len) +  "<br>");
        }
        command=new String("<html>"+ tmp2.toString() + "</html>");
        mypid = hispid;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public String getMypid() {
        return mypid;
    }

    public void setMypid(final String mypid) {
        this.mypid = mypid;
    }
        
    private String command;
    private String mypid;
    
}
