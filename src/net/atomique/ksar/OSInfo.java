/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author alex
 */
public class OSInfo {

    public OSInfo(String s1, String s2) {
        ostype = new String(s1);
        Detect = new String(s2);
    }

    public void setHostname(String s) {
        Hostname = new String(s);
    }

    public void setOSversion(String s) {
        OSversion = new String(s);
    }

    public void setKernel(String s) {
        Kernel = new String(s);
    }

    public void setCpuType(String s) {
        CpuType = new String(s);
    }

    public void setDate(String s) {
        Date dateSimple1;
        Date dateSimple2;
        Date dateSimple3;
        if (sarStartDate == null) {
            sarStartDate = new String(s);
        }
        if (sarEndDate == null) {
            sarEndDate = new String(s);
        }
        try {
            dateSimple1 = new SimpleDateFormat("MM/dd/yy").parse(s);
            dateSimple2 = new SimpleDateFormat("MM/dd/yy").parse(sarStartDate);
            dateSimple3 = new SimpleDateFormat("MM/dd/yy").parse(sarEndDate);
        } catch (ParseException e) {
            return;
        }
        if (dateSimple1.compareTo(dateSimple2) < 0) {
            sarStartDate = new String(s);
        }
        if (dateSimple1.compareTo(dateSimple3) > 0) {
            sarEndDate = new String(s);
        }
    }

    public void setMacAddress(String s) {
        MacAddress = new String(s);
    }

    public void setMemory(String s) {
        Memory = new String(s);
    }

    public void setNBDisk(String s) {
        NBDisk = new String(s);
    }

    public void setNBCpu(String s) {
        NBCpu = new String(s);
    }

    public void setENT(String s) {
        ENT = new String(s);
    }

    public String getDate() {
        if (sarStartDate.equals(sarEndDate)) {
            return sarStartDate;
        } else {
            return sarStartDate + " to " + sarEndDate;
        }
    }

    public String getOSInfo() {
        StringBuffer tmpstr = new StringBuffer();
        tmpstr.append("OS Type: " + ostype + " (" + Detect + " detected)\n");
        if (OSversion != null) {
            tmpstr.append("OS Version: " + OSversion + "\n");
        }
        if (Kernel != null) {
            tmpstr.append("Kernel Release: " + Kernel + "\n");
        }
        if (CpuType != null) {
            tmpstr.append("CPU Type: " + CpuType + "\n");
        }
        if (Hostname != null) {
            tmpstr.append("Hostname: " + Hostname + "\n");
        }
        if (MacAddress != null) {
            tmpstr.append("Mac Address: " + MacAddress + "\n");
        }
        if (Memory != null) {
            tmpstr.append("Memory: " + Memory + "\n");
        }
        if (NBDisk != null) {
            tmpstr.append("Number of disks: " + NBDisk + "\n");
        }
        if (NBCpu != null) {
            tmpstr.append("Number of CPU: " + NBCpu + "\n");
        }
        if (ENT != null) {
            tmpstr.append("Ent: " + ENT + "\n");
        }
        if (sarStartDate != null) {
            tmpstr.append("Start of SAR: " + sarStartDate + "\n");
        }
        if (sarEndDate != null) {
            tmpstr.append("End of SAR: " + sarEndDate + "\n");
        }

        tmpstr.append("\n");

        return tmpstr.toString();
    }
    private String ostype = null;
    private String Hostname = null;
    private String OSversion = null;
    private String Kernel = null;
    private String CpuType = null;
    private String sarStartDate = null;
    private String sarEndDate = null;
    private String MacAddress = null;
    private String Memory = null;
    private String NBDisk = null;
    private String NBCpu = null;
    private String ENT = null;
    private String Detect = null;
}
