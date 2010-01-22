/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Solaris;

import java.util.StringTokenizer;
import javax.swing.tree.DefaultMutableTreeNode;

import net.atomique.ksar.IOsSpecificParser;
import net.atomique.ksar.OSInfo;
import net.atomique.ksar.ParsingException;
import net.atomique.ksar.diskName;
import net.atomique.ksar.kSar;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Second;

/**
 *
 * @author alex, Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class Parser implements IOsSpecificParser {

    public Parser(final kSar hissar) 
    {
        mysar = hissar;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IOsSpecificParser#parseOsInfo(java.lang.String, java.util.StringTokenizer)
     */
    @Override
    public void parseOsInfo(StringTokenizer matcher) {
        if (mysar.myOS == null) {
            mysar.myOS = new OSInfo("SunOS", "automatically");
        }

        this.mysar.hostName = matcher.nextToken();
        mysar.myOS.setHostname(this.mysar.hostName);
        mysar.myOS.setOSversion(matcher.nextToken());
        mysar.myOS.setKernel(matcher.nextToken());
        mysar.myOS.setCpuType(matcher.nextToken());
        
        String sarDate = matcher.nextToken();
        mysar.myOS.setDate(sarDate);
        String[] dateSplit = sarDate.split("/");
        if (dateSplit.length == 3) {
            mysar.day = Integer.parseInt(dateSplit[1]);
            mysar.month = Integer.parseInt(dateSplit[0]);
            mysar.year = Integer.parseInt(dateSplit[2]);
            if (mysar.year < 100) { // solaris 8 show date on two digit
                mysar.year += 2000;
            }
        }
        
        mysar.setPageSize();
    }
    
    /* (non-Javadoc)
     * @see net.atomique.ksar.IOsSpecificParser#parse(java.lang.String, java.lang.String, java.util.StringTokenizer)
     */
    @Override
    public void parse(final String thisLine,final String first,final StringTokenizer matcher) throws ParsingException {
        boolean headerFound = false;
        // match some header line
        if (thisLine.indexOf("%usr") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("device") > 0) {
            headerFound=true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("runq-sz") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("bread/s") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("swpin/s") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("scall/s") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("iget/s") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("rawch/s") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("proc-sz") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("msg/s") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("atch/s") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("pgout/s") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("freemem") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("sml_mem") > 0) {
            headerFound = true;
            mysar.underaverage = 0;
        }
        
        // parse time or continue except device line that are missing the time entry
        final String[] sarTime = first.split(":");
        if (sarTime.length != 3 && ! "device".equals(statType)) {
            return;
        } else {
            if (sarTime.length == 3) {
                heure = Integer.parseInt(sarTime[0]);
                minute = Integer.parseInt(sarTime[1]);
                seconde = Integer.parseInt(sarTime[2]);
                now = new Second(seconde, minute, heure, mysar.day, mysar.month, mysar.year);
                if (mysar.statstart == null) {
                    mysar.statstart = new String(now.toString());                   
                    mysar.startofgraph = now;
                }
                if ( ! mysar.datefound.contains(now) ) {
                    mysar.datefound.add(now);
                }
                if (now.compareTo(mysar.lastever) > 0) {
                    mysar.lastever = now;
                    mysar.statend = new String(mysar.lastever.toString());
                    mysar.endofgraph = mysar.lastever;
                }
                firstwastime = 1;
            } else {
                firstwastime = 0;
            }
        }

        if (matcher.hasMoreElements()) {
            lastHeader = matcher.nextToken();
        } else {
            return;
        }
        // was a header ?
        if (headerFound) {
            if (lastHeader.equals(statType)) {
                headerFound = false;
                return;
            }

            statType = lastHeader;

            if ("%usr".equals(lastHeader)) {
                if (sarCPU == null) {
                    sarCPU = new cpuSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarCPU.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolariscpuSar", sarCPU);
                    sarCPU.setGraphLink("SolariscpuSar");
                }
                return;
            } else
            if ("device".equals(lastHeader)) {
                if ( ! mysar.hasdisknode) {
                    if ( mysar.myUI != null ) {
                        mysar.add2tree(mysar.graphtree,mysar.diskstreenode);
                    }
                    mysar.hasdisknode = true;
                }
                return;
            } else
            if ("runq-sz".equals(lastHeader)) {
                if (sarRQUEUE == null) {
                    sarRQUEUE = new rqueueSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarRQUEUE.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolarisRqueueSar", sarRQUEUE);
                    sarRQUEUE.setGraphLink("SolarisRqueueSar");
                }
                if (sarSQUEUE == null) {
                    sarSQUEUE = new squeueSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarSQUEUE.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolarisSqueueSar", sarSQUEUE);
                    sarSQUEUE.setGraphLink("SolarisSqueueSar");
                }
                return;
            } else
            if ("bread/s".equals(lastHeader)) {
                if (sarBUFFER == null) {
                    sarBUFFER = new bufferSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarBUFFER.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolarisbufferSar", sarBUFFER);
                    sarBUFFER.setGraphLink("SolarisbufferSar");
                }
                return;
            } else
            if ("swpin/s".equals(lastHeader)) {
                if (sarSWAP == null) {
                    sarSWAP = new swapingSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarSWAP.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolarisswapingSar", sarSWAP);
                    sarSWAP.setGraphLink("SolarisswapSar");
                }
                return;
            } else
            if ("scall/s".equals(lastHeader)) {
                if (sarSYSCALL == null) {
                    sarSYSCALL = new syscallSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarSYSCALL.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolarissyscalSar", sarSYSCALL);
                    sarSYSCALL.setGraphLink("SolarissyscalSar");
                }
                return;
            } else
            if ("iget/s".equals(lastHeader)) {
                if (sarFILE == null) {
                    sarFILE = new fileSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarFILE.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolarisfileSar", sarFILE);
                    sarFILE.setGraphLink("SolarisfileSar");
                }
                return;
            } else
            if ("rawch/s".equals(lastHeader)) {
                if (sarTTY == null) {
                    sarTTY = new ttySar(mysar);
                    if ( mysar.myUI != null ) {
                        sarTTY.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolaristtySar", sarTTY);
                    sarTTY.setGraphLink("SolaristtySar");
                }
                return;
            } else
            if ("proc-sz".equals(lastHeader)) {
                return;
            } else
            if ("msg/s".equals(lastHeader)) {
                if (sarMSG == null) {
                    sarMSG = new msgSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarMSG.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolarismsgSar", sarMSG);
                    sarMSG.setGraphLink("SolarismsgSar");
                }
                return;
            } else
            if ("atch/s".equals(lastHeader)) {
                if (sarPAGING2 == null) {
                    sarPAGING2 = new paging2Sar(mysar);
                    if ( mysar.myUI != null ) {
                        sarPAGING2.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolarispagingSar2", sarPAGING2);
                    sarPAGING2.setGraphLink("SolarispagingSar2");
                }
                return;
            } else
            if ("pgout/s".equals(lastHeader)) {
                if (sarPAGING1 == null) {
                    sarPAGING1 = new paging1Sar(mysar);
                    if ( mysar.myUI != null ) {
                        sarPAGING1.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolarispagingSar1", sarPAGING1);
                    sarPAGING2.setGraphLink("SolarispagingSar1");
                }
                return;
            } else
            if ("freemem".equals(lastHeader)) {
                if (sarMEM == null) {
                    sarMEM = new memSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarMEM.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolarismemSar", sarMEM);
                    sarMEM.setGraphLink("SolarismemSar");
                }
                return;
            } else
            if ("sml_mem".equals(lastHeader)) {
                if (sarKMASML == null) {
                    sarKMASML = new kmasmlSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarKMASML.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolariskmasmlSar", sarKMASML);
                    sarKMASML.setGraphLink("SolariskmasmlSar");
                }
                if (sarKMALG == null) {
                    sarKMALG = new kmalgSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarKMALG.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolariskmalgSar", sarKMALG);
                    sarKMALG.setGraphLink("SolariskmalgSar");
                }
                if (sarKMAOVZ == null) {
                    sarKMAOVZ = new kmaovzSar(mysar);
                    if ( mysar.myUI != null ) {
                        sarKMAOVZ.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SolariskmaovzSar", sarKMAOVZ);
                    sarKMAOVZ.setGraphLink("SolariskmaovzSar");
                }
                return;
            }
            headerFound = false;
            return;
        }

        // for CPU
        try {
            if ("%usr".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                sarCPU.add(now, val1, val2, val3, val4);
                return;
            } else
            if ("swpin/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                sarSWAP.add(now, val1, val2, val3, val4, val5);
                return;
            } else
            if ("freemem".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                sarMEM.add(now, val1, val2);
                return;
            } else
            if ("scall/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                sarSYSCALL.add(now, val1, val2, val3, val4, val5, val6, val7);
                return;
            } else
            if ("iget/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                sarFILE.add(now, val1, val2, val3);
                return;
            } else
            if ("msg/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                sarMSG.add(now, val1, val2);
                return;
            } else
            if ("pgout/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                sarPAGING1.add(now, val1, val2, val3, val4, val5);
                return;
            } else
            if ("proc-sz".equals(statType)) {
                return;
            } else
            if ("atch/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                sarPAGING2.add(now, val1, val2, val3, val4, val5, val6);
                return;
            } else
            if ("rawch/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                sarTTY.add(now, val1, val2, val3, val4, val5, val6);
                return;
            } else
            if ("bread/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                sarBUFFER.add(now, val1, val2, val3, val4, val5, val6, val7, val8);
                return;
            } else
            if ("runq-sz".equals(statType)) {
                if (matcher.hasMoreElements()) {
                    val1 = new Float(lastHeader);
                    val2 = new Float(matcher.nextToken());
                    sarRQUEUE.add(now, val1, val2);
                } else {
                    return;
                }
                if (matcher.hasMoreElements()) {
                    val3 = new Float(matcher.nextToken());
                    val4 = new Float(matcher.nextToken());
                    sarSQUEUE.add(now, val3, val4);

                }
                return;
            } else
            if ("sml_mem".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                sarKMASML.add(now, val1, val2, val3);
                sarKMALG.add(now, val4, val5, val6);
                sarKMAOVZ.add(now, val7, val8);
                return;
            } else
            if ("device".equals(statType)) {
                if (mysar.underaverage == 1) {
                    return;
                }
                diskxferSar mydiskxfer=null;
                diskwaitSar mydiskwait=null;
                if (firstwastime == 1) {
                    mydiskxfer = (diskxferSar)mysar.disksSarList.get(lastHeader + "Solarisxfer");
                    if( mydiskxfer == null) {
                        diskName tmp = new diskName(lastHeader);
                        mysar.AlternateDiskName.put(lastHeader, tmp);
                        tmp.setTitle((String) mysar.Adiskname.get(lastHeader));
                        mydiskxfer = new diskxferSar(mysar, lastHeader, tmp);
                        mysar.disksSarList.put(lastHeader + "Solarisxfer", mydiskxfer);
                        mydiskwait = new diskwaitSar(mysar, lastHeader, tmp);
                        mysar.disksSarList.put(lastHeader + "Solariswait", mydiskwait);
                        DefaultMutableTreeNode mydisk = new DefaultMutableTreeNode(tmp.showTitle());                        
                        mysar.pdfList.put(lastHeader + "Solarisxfer", mydiskxfer);
                        mydiskxfer.addtotree(mydisk);
                        mydiskwait.addtotree(mydisk);
                        mysar.pdfList.put(lastHeader + "Solariswait", mydiskwait);
                        mydiskxfer.setGraphLink(lastHeader + "Solarisxfer");
                        mydiskwait.setGraphLink(lastHeader + "Solariswait");
                        mysar.add2tree(mysar.diskstreenode,mydisk);
                    } else {
                        mydiskwait = (diskwaitSar) mysar.disksSarList.get(lastHeader + "Solariswait");
                    }
                    val1 = new Float(matcher.nextToken());
                    val2 = new Float(matcher.nextToken());
                    val3 = new Float(matcher.nextToken());
                    val4 = new Float(matcher.nextToken());
                    val5 = new Float(matcher.nextToken());
                    val6 = new Float(matcher.nextToken());
                    mydiskxfer.add(now, val4, val3, val6);
                    mydiskwait.add(now, val2, val5, val1);
                    return;
                } else {
                    mydiskxfer = (diskxferSar) mysar.disksSarList.get(first + "Solarisxfer");
                    if ( mydiskxfer == null ) {
                        diskName tmp = new diskName(first);
                        mysar.AlternateDiskName.put(first, tmp);
                        tmp.setTitle((String) mysar.Adiskname.get(first));
                        mydiskxfer = new diskxferSar(mysar, first, tmp);
                        mysar.disksSarList.put(first + "Solarisxfer", mydiskxfer);
                        mydiskwait = new diskwaitSar(mysar, first, tmp);
                        mysar.disksSarList.put(first + "Solariswait", mydiskwait);
                        DefaultMutableTreeNode mydisk = new DefaultMutableTreeNode(tmp.showTitle());
                        mydiskxfer.addtotree(mydisk);
                        mydiskwait.addtotree(mydisk);
                        mysar.pdfList.put(first + "Solarisxfer", mydiskxfer);
                        mysar.pdfList.put(first + "Solariswait", mydiskwait);
                        mydiskxfer.setGraphLink(first + "Solarisxfer");
                        mydiskwait.setGraphLink(first + "Solariswait");
                        mysar.add2tree(mysar.diskstreenode,mydisk);                        
                    } else {
                        mydiskwait = (diskwaitSar) mysar.disksSarList.get(first + "Solariswait");
                    }
                    val1 = new Float(lastHeader);
                    val2 = new Float(matcher.nextToken());
                    val3 = new Float(matcher.nextToken());
                    val4 = new Float(matcher.nextToken());
                    val5 = new Float(matcher.nextToken());
                    val6 = new Float(matcher.nextToken());
                    mydiskxfer.add(now, val4, val3, val6);
                    mydiskwait.add(now, val2, val5, val1);
                    return;
                }
            }
        } catch (SeriesException e) {
            throw new ParsingException("Solaris parser: " + e);
        }
    }
    
    final private kSar mysar;
    private Float val1;
    private Float val2;
    private Float val3;
    private Float val4;
    private Float val5;
    private Float val6;
    private Float val7;
    private Float val8;
    private int heure = 0;
    private int minute = 0;
    private int seconde = 0;
    private Second now = new Second(0, 0, 0, 1, 1, 1970);
    private String lastHeader;
    private String statType = "none";
    private int firstwastime;
    private cpuSar sarCPU = null;
    private swapingSar sarSWAP = null;
    private memSar sarMEM = null;
    private syscallSar sarSYSCALL = null;
    private fileSar sarFILE = null;
    private msgSar sarMSG = null;
    private paging1Sar sarPAGING1 = null;
    private paging2Sar sarPAGING2 = null;
    private ttySar sarTTY = null;
    private bufferSar sarBUFFER = null;
    private squeueSar sarSQUEUE = null;
    private rqueueSar sarRQUEUE = null;
    private kmasmlSar sarKMASML = null;
    private kmalgSar sarKMALG = null;
    private kmaovzSar sarKMAOVZ = null;
}
