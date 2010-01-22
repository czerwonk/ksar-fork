/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Hpux;

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

    public Parser(kSar hissar) {
        mysar = hissar;
    }
    
    
   
    /* (non-Javadoc)
     * @see net.atomique.ksar.IOsSpecificParser#parseOsInfo(java.util.StringTokenizer)
     */
    @Override
    public void parseOsInfo(StringTokenizer matcher) {
        if (this.mysar.myOS == null) {
            this.mysar.myOS = new OSInfo("HP-UX", "automatically");
        }
        this.mysar.hostName = matcher.nextToken();
        this.mysar.myOS.setHostname(this.mysar.hostName);
        this.mysar.myOS.setOSversion(matcher.nextToken());
        this.mysar.myOS.setKernel(matcher.nextToken());
        this.mysar.myOS.setCpuType(matcher.nextToken());
        
        String sarDate = matcher.nextToken();
        this.mysar.myOS.setDate(sarDate);
        String[] dateSplit = sarDate.split("/");
        if (dateSplit.length == 3) {
            this.mysar.day = Integer.parseInt(dateSplit[1]);
            this.mysar.month = Integer.parseInt(dateSplit[0]);
            this.mysar.year = Integer.parseInt(dateSplit[2]);
            if (this.mysar.year < 100) { // solaris 8 show date on two digit
                this.mysar.year += 2000;
            }
        }
        
        this.mysar.parseAlternatediskname();
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IOsSpecificParser#parse(java.lang.String, java.lang.String, java.util.StringTokenizer)
     */
    @Override
    public void parse(String thisLine, String first, StringTokenizer matcher) throws ParsingException {
        int headerFound = 0;
        // match some header line
        if (thisLine.indexOf("%usr") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("device") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("runq-sz") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("bread/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("swpin/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("scall/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("iget/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("rawch/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("proc-sz") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("msg/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("atch/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("pgout/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("freemem") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("sml_mem") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }

        // parse time or continue except device line that are missing the time entry
        String[] sarTime = first.split(":");
        if (sarTime.length != 3 && !statType.equals("device")) {
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
        if (headerFound == 1) {
            if (lastHeader.equals(statType)) {
                headerFound = 0;
                return;
            }

            statType = lastHeader;

            if (lastHeader.equals("%usr")) {
                if (sarCPU == null) {
                    sarCPU = new cpuSar(mysar);
                    if (mysar.myUI != null) {
                        sarCPU.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("HpuxcpuSar", sarCPU);
                    sarCPU.setGraphLink("HpuxcpuSar");
                }
                return;
            }
            if (lastHeader.equals("device")) {
                if ( ! mysar.hasdisknode ) {
                    if (mysar.myUI != null) {
                        mysar.add2tree(mysar.graphtree,mysar.diskstreenode);
                    }
                    mysar.hasdisknode = true;
                }
                return;
            }
            if (lastHeader.equals("runq-sz")) {
                if (sarRQUEUE == null) {
                    sarRQUEUE = new rqueueSar(mysar);
                    if (mysar.myUI != null) {
                        sarRQUEUE.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("HpuxRqueueSar", sarRQUEUE);
                    sarRQUEUE.setGraphLink("HpuxRqueueSar");
                }
                if (sarSQUEUE == null) {
                    sarSQUEUE = new squeueSar(mysar);
                    if (mysar.myUI != null) {
                        sarSQUEUE.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("HpuxSqueueSar", sarSQUEUE);
                    sarSQUEUE.setGraphLink("HpuxSqueueSar");
                }
                return;
            }
            if (lastHeader.equals("bread/s")) {
                if (sarBUFFER == null) {
                    sarBUFFER = new bufferSar(mysar);
                    if (mysar.myUI != null) {
                        sarBUFFER.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("HpuxbufferSar", sarBUFFER);
                    sarBUFFER.setGraphLink("HpuxbufferSar");
                }
                return;
            }
            if (lastHeader.equals("swpin/s")) {
                if (sarSWAP == null) {
                    sarSWAP = new swapingSar(mysar);
                    if (mysar.myUI != null) {
                        sarSWAP.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("HpuxswapingSar", sarSWAP);
                    sarSWAP.setGraphLink("HpuxswapSar");
                }
                return;
            }
            if (lastHeader.equals("scall/s")) {
                if (sarSYSCALL == null) {
                    sarSYSCALL = new syscallSar(mysar);
                    if (mysar.myUI != null) {
                        sarSYSCALL.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("syscalSar", sarSYSCALL);
                    sarSYSCALL.setGraphLink("HpuxsyscalSar");
                }
                return;
            }
            if (lastHeader.equals("iget/s")) {
                if (sarFILE == null) {
                    sarFILE = new fileSar(mysar);
                    if (mysar.myUI != null) {
                        sarFILE.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("HpuxfileSar", sarFILE);
                    sarFILE.setGraphLink("HpuxfileSar");
                }
                return;
            }
            if (lastHeader.equals("rawch/s")) {
                if (sarTTY == null) {
                    sarTTY = new ttySar(mysar);
                    if (mysar.myUI != null) {
                        sarTTY.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("HpuxttySar", sarTTY);
                    sarTTY.setGraphLink("HpuxttySar");
                }
                return;
            }
            if (lastHeader.equals("proc-sz")) {
                return;
            }

            if (lastHeader.equals("msg/s")) {
                if (sarMSG == null) {
                    sarMSG = new msgSar(mysar);
                    if (mysar.myUI != null) {
                        sarMSG.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("HpuxmsgSar", sarMSG);
                    sarMSG.setGraphLink("HpuxmsgSar");
                }
                return;
            }

            headerFound = 0;
            return;
        }

        // for CPU
        try {
            if (statType.equals("%usr")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                sarCPU.add(now, val1, val2, val3, val4);
                return;
            }
            if (statType.equals("swpin/s")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                sarSWAP.add(now, val1, val2, val3, val4, val5);
                return;
            }
            if (statType.equals("scall/s")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                sarSYSCALL.add(now, val1, val2, val3, val4, val5, val6, val7);
                return;
            }
            if (statType.equals("iget/s")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                sarFILE.add(now, val1, val2, val3);
                return;
            }
            if (statType.equals("msg/s")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                sarMSG.add(now, val1, val2);
                return;
            }
            if (statType.equals("rawch/s")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                sarTTY.add(now, val1, val2, val3, val4, val5, val6);
                return;
            }
            if (statType.equals("bread/s")) {
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
            }
            if (statType.equals("runq-sz")) {
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
            }
            if (statType.equals("device")) {
                if (mysar.underaverage == 1) {
                    return;
                }
                diskxferSar mydiskxfer;
                diskwaitSar mydiskwait;
                if (firstwastime == 1) {
                    if (!mysar.disksSarList.containsKey(lastHeader + "Hpuxxfer")) {
                        diskName tmp = new diskName(lastHeader);
                        mysar.AlternateDiskName.put(lastHeader, tmp);
                        tmp.setTitle((String) mysar.Adiskname.get(lastHeader));
                        mydiskxfer = new diskxferSar(mysar, lastHeader, tmp);
                        mysar.disksSarList.put(lastHeader + "Hpuxxfer", mydiskxfer);
                        mydiskwait = new diskwaitSar(mysar, lastHeader, tmp);
                        mysar.disksSarList.put(lastHeader + "Hpuxwait", mydiskwait);
                        DefaultMutableTreeNode mydisk = new DefaultMutableTreeNode(tmp.showTitle());
                        mydiskxfer.addtotree(mydisk);
                        mydiskwait.addtotree(mydisk);
                        mysar.pdfList.put(lastHeader + "Hpuxxfer", mydiskxfer);
                        mysar.pdfList.put(lastHeader + "Hpuxwait", mydiskwait);
                        mydiskxfer.setGraphLink(lastHeader + "Hpuxxfer");
                        mydiskwait.setGraphLink(lastHeader + "Hpuxwait");
                        mysar.add2tree(mysar.diskstreenode,mydisk);
                    } else {
                        mydiskxfer = (diskxferSar) mysar.disksSarList.get(lastHeader + "Hpuxxfer");
                        mydiskwait = (diskwaitSar) mysar.disksSarList.get(lastHeader + "Hpuxwait");
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
                    if (!mysar.disksSarList.containsKey(first + "Hpuxxfer")) {
                        diskName tmp = new diskName(first);
                        mysar.AlternateDiskName.put(first, tmp);
                        tmp.setTitle((String) mysar.Adiskname.get(first));
                        mydiskxfer = new diskxferSar(mysar, first, tmp);
                        mysar.disksSarList.put(first + "Hpuxxfer", mydiskxfer);
                        mydiskwait = new diskwaitSar(mysar, first, tmp);
                        mysar.disksSarList.put(first + "Hpuxwait", mydiskwait);
                        DefaultMutableTreeNode mydisk = new DefaultMutableTreeNode(tmp.showTitle());
                        mydiskxfer.addtotree(mydisk);
                        mydiskwait.addtotree(mydisk);
                        mysar.pdfList.put(first + "Hpuxxfer", mydiskxfer);
                        mysar.pdfList.put(first + "Hpuxwait", mydiskwait);
                        mydiskxfer.setGraphLink(first + "Hpuxxfer");
                        mydiskwait.setGraphLink(first + "Hpuxwait");
                        mysar.add2tree(mysar.diskstreenode,mydisk);
                    } else {
                        mydiskxfer = (diskxferSar) mysar.disksSarList.get(first + "Hpuxxfer");
                        mydiskwait = (diskwaitSar) mysar.disksSarList.get(first + "Hpuxwait");
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
            throw new ParsingException("Hpux parser: " + e);
        }
    }
    
    kSar mysar;
    Float val1;
    Float val2;
    Float val3;
    Float val4;
    Float val5;
    Float val6;
    Float val7;
    Float val8;
    Float val9;
    Float val10;
    Float val11;
    int heure = 0;
    int minute = 0;
    int seconde = 0;
    Second now = new Second(0, 0, 0, 1, 1, 1970);
    String lastHeader;
    String statType = "none";
    int firstwastime;
    cpuSar sarCPU = null;
    swapingSar sarSWAP = null;
    syscallSar sarSYSCALL = null;
    fileSar sarFILE = null;
    msgSar sarMSG = null;
    ttySar sarTTY = null;
    bufferSar sarBUFFER = null;
    squeueSar sarSQUEUE = null;
    rqueueSar sarRQUEUE = null;
}
