/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.AIX;

import java.util.StringTokenizer;
import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.diskName;
import net.atomique.ksar.kSar;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Second;

/**
 *
 * @author alex
 */
public class Parser {

    public Parser(final kSar hissar) {
        mysar = hissar;
    }

    public int parse(final String thisLine,final  String prems,final StringTokenizer matcher) {
        String first = prems;
        int headerFound = 0;
        // match some header line
        
        if (thisLine.indexOf("lookuppn") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("bread/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("scall/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("device") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("ksched") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("msg") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("runq-sz") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("slots") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("%usr") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("proc-sz") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("cswch") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("rawch") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }

        // parse time or continue except device line that are missing the time entry
        String[] sarTime = first.split(":");
        if (sarTime.length != 3 && !("device".equals(statType) || "Kbs/s".equals(statType) ||
                "cpu-iget/s".equals(statType) || "cpu-%usr".equals(statType) || "cpu-scall/s".equals(statType) ||
                "cpu-cswch/s".equals(statType) || "cpu-msg/s".equals(statType) ||
                "cpu-physc".equals(statType) || "cpu-%entc".equals(statType))) {
            
            return 1;
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
                if (!mysar.datefound.contains(now)) {
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
            return 1;
        }
        // was a header ?
        if (headerFound == 1) {
            if (lastHeader.equals(statType)) {
                headerFound = 0;
                return 1;
            }
            
            if ( lastHeader.equals("device") && thisLine.indexOf("Kbs/s") > 0 && statType.equals("Kbs/s") ) {
            headerFound=0;
            return 1;
            }
            //%entc
            if ( lastHeader.equals("%usr") && thisLine.indexOf("%entc") > 0 && statType.equals("%entc") ) {
            headerFound=0;
            return 1;
            }
            //physc
            if ( lastHeader.equals("%usr") && thisLine.indexOf("physc") > 0 && statType.equals("physc") ) {
            headerFound=0;
            return 1;
            }
             
            statType = lastHeader;

            if ("%usr".equals(lastHeader)) {
                if (sarCPU3 == null) {
                    sarCPU3 = new cpuSar(mysar, "");
                    if (mysar.myUI != null) {
                        sarCPU3.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("AixcpuSar", sarCPU3);
                    sarCPU3.setGraphLink("AixcpuSar");
                }
                if (thisLine.indexOf("physc") > 0) {
                    statType = "physc";
                    sarCPU3.setcpuOpt("physc");
                }
                if (thisLine.indexOf("%entc") > 0) {
                    statType = "%entc";
                    sarCPU3.setcpuOpt("%entc");
                }
                return 1;
            }
            if ("bread/s".equals(lastHeader)) {
                if (sarBUFFER3 == null) {
                    sarBUFFER3 = new bufferSar(mysar);
                    if (mysar.myUI != null) {
                        sarBUFFER3.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("AixbufferSar", sarBUFFER3);
                    sarBUFFER3.setGraphLink("AixbufferSar");
                }
                return 1;
            }
            if ("runq-sz".equals(lastHeader)) {
                if (sarRQUEUE3 == null) {
                    sarRQUEUE3 = new rqueueSar(mysar);
                    if (mysar.myUI != null) {
                        sarRQUEUE3.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("AixRqueueSar", sarRQUEUE3);
                    sarRQUEUE3.setGraphLink("AixRqueueSar");
                }
                if (sarSQUEUE3 == null) {
                    sarSQUEUE3 = new squeueSar(mysar);
                    if (mysar.myUI != null) {
                        sarSQUEUE3.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("SqueueSar", sarSQUEUE3);
                    sarSQUEUE3.setGraphLink("AixSqueueSar");
                }
                return 1;
            }
            if ("ksched/s".equals(lastHeader)) {
                if (sarKERNEL3 == null) {
                    sarKERNEL3 = new kernelSar(mysar);
                    if (mysar.myUI != null) {
                        sarKERNEL3.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("kernelSar", sarKERNEL3);
                    sarKERNEL3.setGraphLink("AixkernelSar");
                }
                return 1;
            }
            if ("rawch/s".equals(lastHeader)) {
                if (sarTTY3 == null) {
                    sarTTY3 = new ttySar(mysar);
                    if (mysar.myUI != null) {
                        sarTTY3.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("ttySar", sarTTY3);
                    sarTTY3.setGraphLink("AixttySar");
                }
                return 1;
            }
            if ("msg/s".equals(lastHeader)) {
                if (sarMSG3 == null) {
                    sarMSG3 = new msgSar(mysar, "");
                    if (mysar.myUI != null) {
                        sarMSG3.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("AixmsgSar", sarMSG3);
                    sarMSG3.setGraphLink("AixmsgSar");
                }
                return 1;
            }
            if ("scall/s".equals(lastHeader)) {
                if (sarSYSCALL3 == null) {
                    sarSYSCALL3 = new syscallSar(mysar, "");
                    if (mysar.myUI != null) {
                        sarSYSCALL3.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("AixsyscalSar", sarSYSCALL3);
                    sarSYSCALL3.setGraphLink("AixsyscallSar");
                }
                return 1;
            }
            if ("cswch/s".equals(lastHeader)) {
                if (sarCSWCH3 == null) {
                    sarCSWCH3 = new cswchSar(mysar, "");
                    if (mysar.myUI != null) {
                        sarCSWCH3.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("AixcswchSar", sarCSWCH3);
                    sarCSWCH3.setGraphLink("AixcswchSar");
                }
                return 1;
            }
            if ("iget/s".equals(lastHeader)) {
                if (sarFILE3 == null) {
                    sarFILE3 = new fileSar(mysar, "");
                    if (mysar.myUI != null) {
                        sarFILE3.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("AixfileSar", sarFILE3);
                    sarFILE3.setGraphLink("AixfileSar");
                }
                return 1;
            }
            if ("slots".equals(lastHeader)) {
                if (sarSLOT3 == null) {
                    sarSLOT3 = new slotSar(mysar);
                    if (mysar.myUI != null) {
                        sarSLOT3.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("AixslotSar", sarSLOT3);
                    sarSLOT3.setGraphLink("AixslotSar");
                }
                return 1;
            }
            if ("device".equals(lastHeader)) {
                if (!mysar.hasdisknode) {
                    if (mysar.myUI != null) {
                        mysar.add2tree(mysar.graphtree, mysar.diskstreenode);
                    }
                    mysar.hasdisknode = true;
                }
                if (thisLine.indexOf("Kbs/s") > 0) {
                    statType = new String("Kbs/s");
                }
                if (thisLine.indexOf("scall/s") > 0) {
                    statType = new String("scall/s");
                }
                return 1;
            }
            /* 
            this one is a special one it handle the -P ALL
            and subsequence counter.so this is not only for cpu stuff (usr/sys./...)
             */

            if ("cpu".equals(lastHeader)) {
                /*  only cpu only got %usr so we have cpu */
                if (thisLine.indexOf("%usr") > 0) {
                    if (!mysar.hascpunode) {
                        if (mysar.myUI != null) {
                            mysar.add2tree(mysar.graphtree, mysar.cpustreenode);
                        }
                        mysar.hascpunode = true;
                    }
                    statType = new String("cpu-%usr");
                    if (thisLine.indexOf("physc") > 0) {
                        statType = new String("cpu-physc");
                    }
                    if (thisLine.indexOf("%entc") > 0) {
                        statType = new String("cpu-%entc");
                    }
                    return 1;
                }
                if (thisLine.indexOf("iget") > 0) {
                    if (!mysar.hasfilenode) {
                        if (mysar.myUI != null) {
                            mysar.add2tree(mysar.graphtree, mysar.filetreenode);
                        }
                        mysar.hasfilenode = true;
                    }
                    statType = new String("cpu-iget/s");
                    return 1;
                }
                if (thisLine.indexOf("msg") > 0) {
                    if (!mysar.hasmsgnode) {
                        if (mysar.myUI != null) {
                            mysar.add2tree(mysar.graphtree, mysar.msgtreenode);
                        }
                        mysar.hasmsgnode = true;
                    }
                    statType = new String("cpu-msg/s");
                    return 1;
                }
                if (thisLine.indexOf("cswch") > 0) {
                    if (!mysar.hascswchnode) {
                        if (mysar.myUI != null) {
                            mysar.add2tree(mysar.graphtree, mysar.cswchtreenode);
                        }
                        mysar.hascswchnode = true;
                    }
                    statType = new String("cpu-cswch/s");
                    return 1;
                }
                if (thisLine.indexOf("scall") > 0) {
                    if (!mysar.hasscallnode) {
                        if (mysar.myUI != null) {
                            mysar.add2tree(mysar.graphtree, mysar.scalltreenode);
                        }
                        mysar.hasscallnode = true;
                    }
                    statType = new String("cpu-scall/s");
                    return 1;
                }
                return 1;
            }
            /* end of special case -PALL */

            if ("proc-sz".equals(lastHeader)) {
                return 1;
            }
            headerFound = 0;
            return 1;
        }

        try {
            if ("%usr".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                sarCPU3.add(now, val1, val2, val3, val4);
                return 1;
            }
            if ("physc".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                sarCPU3.add(now, val1, val2, val3, val4, val5);
                return 1;
            }
            if ("%entc".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                sarCPU3.add(now, val1, val2, val3, val4, val5, val6);
                return 1;
            }
            if ("bread/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                sarBUFFER3.add(now, val1, val2, val3, val4, val5, val6, val7, val8);
                return 1;
            }
            if ("runq-sz".equals(statType)) {
                if (matcher.hasMoreElements()) {
                    val1 = new Float(lastHeader);
                    val2 = new Float(matcher.nextToken());
                    sarRQUEUE3.add(now, val1, val2);
                } else {
                    return 1;
                }
                if (matcher.hasMoreElements()) {
                    val3 = new Float(matcher.nextToken());
                    val4 = new Float(matcher.nextToken());
                    sarSQUEUE3.add(now, val3, val4);

                }
                return 1;
            }
            if ("scall/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                sarSYSCALL3.add(now, val1, val2, val3, val4, val5, val6, val7);
                return 1;
            }
            if ("msg/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                sarMSG3.add(now, val1, val2);
                return 1;
            }
            if ("rawch/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                sarTTY3.add(now, val1, val2, val3, val4, val5, val6);
                return 1;
            }
            if ("slots".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                sarSLOT3.add(now, val1, val2, val3, val4);
                return 1;
            }
            if ("proc-sz".equals(statType)) {
                return 1;
            }
            if ("cswch/s".equals(statType)) {
                val1 = new Float(lastHeader);
                sarCSWCH3.add(now, val1);
                return 1;
            }
            if ("iget/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                sarFILE3.add(now, val1, val2, val3);
                return 1;
            }
            if ("cpu-%usr".equals(statType) || "cpu-%entc".equals(statType) || "cpu-physc".equals(statType)) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                cpuSar mycpusar;
                if (firstwastime == 1) {
                    if ("-".equals(lastHeader)) {
                        lastHeader = "all";
                    }
                    if (mysar.cpuSarList.containsKey(lastHeader + "-cpu")) {
                        mycpusar = (cpuSar) mysar.cpuSarList.get(lastHeader + "-cpu");
                    } else {
                        mycpusar = new cpuSar(mysar, lastHeader);
                        mycpusar.setcpuOpt(statType);
                        DefaultMutableTreeNode mycpu = new DefaultMutableTreeNode("cpu-" + lastHeader);
                        mysar.cpuSarList.put(lastHeader + "-cpu", mycpusar);
                        mysar.pdfList.put(lastHeader + "-cpu", mycpusar);
                        mycpusar.setGraphLink(lastHeader + "-cpu");
                        //mysar.cputreenode.add(mycpusar);
                        if (mysar.myUI != null) {
                            mycpusar.addtotree(mysar.cpustreenode);
                        }
                    }
                    if ("cpu-%usr".equals(statType)) {
                        val1 = new Float(matcher.nextToken());
                        val2 = new Float(matcher.nextToken());
                        val3 = new Float(matcher.nextToken());
                        val4 = new Float(matcher.nextToken());
                        mycpusar.add(now, val1, val2, val3, val4);
                        return 1;
                    }
                    if ("cpu-physc".equals(statType)) {
                        val1 = new Float(matcher.nextToken());
                        val2 = new Float(matcher.nextToken());
                        val3 = new Float(matcher.nextToken());
                        val4 = new Float(matcher.nextToken());
                        val5 = new Float(matcher.nextToken());
                        mycpusar.add(now, val1, val2, val3, val4, val5);
                        return 1;
                    }
                    if ("cpu-%entc".equals(statType)) {
                        val1 = new Float(matcher.nextToken());
                        val2 = new Float(matcher.nextToken());
                        val3 = new Float(matcher.nextToken());
                        val4 = new Float(matcher.nextToken());
                        val5 = new Float(matcher.nextToken());
                        val6 = new Float(matcher.nextToken());
                        mycpusar.add(now, val1, val2, val3, val4, val5, val6);
                        return 1;
                    }
                } else {
                    if ("-".equals(first)) {
                        first = "all";
                    }
                    if (mysar.cpuSarList.containsKey(first + "-cpu")) {
                        mycpusar = (cpuSar) mysar.cpuSarList.get(first + "-cpu");
                    } else {
                        mycpusar = new cpuSar(mysar, first);
                        mycpusar.setcpuOpt(statType);
                        DefaultMutableTreeNode mycpu = new DefaultMutableTreeNode("cpu-" + lastHeader);
                        mysar.cpuSarList.put(first + "-cpu", mycpusar);
                        mysar.pdfList.put(first + "-cpu", mycpusar);
                        mycpusar.setGraphLink(first + "-cpu");
                        //mysar.cputreenode.add(mycpusar);
                        if (mysar.myUI != null) {
                            mycpusar.addtotree(mysar.cpustreenode);
                        }
                    }
                    if ("cpu-%usr".equals(statType)) {
                        val1 = new Float(lastHeader);
                        val2 = new Float(matcher.nextToken());
                        val3 = new Float(matcher.nextToken());
                        val4 = new Float(matcher.nextToken());
                        mycpusar.add(now, val1, val2, val3, val4);
                        return 1;
                    }
                    if ("cpu-physc".equals(statType)) {
                        val1 = new Float(lastHeader);
                        val2 = new Float(matcher.nextToken());
                        val3 = new Float(matcher.nextToken());
                        val4 = new Float(matcher.nextToken());
                        val5 = new Float(matcher.nextToken());
                        mycpusar.add(now, val1, val2, val3, val4, val5);
                        return 1;
                    }
                    if ("cpu-%entc".equals(statType)) {
                        val1 = new Float(lastHeader);
                        val2 = new Float(matcher.nextToken());
                        val3 = new Float(matcher.nextToken());
                        val4 = new Float(matcher.nextToken());
                        val5 = new Float(matcher.nextToken());
                        val6 = new Float(matcher.nextToken());
                        mycpusar.add(now, val1, val2, val3, val4, val5, val6);
                        return 1;
                    }
                }
            }

            if ("cpu-iget/s".equals(statType)) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                fileSar myfilesar;
                if (firstwastime == 1) {
                    if ("-".equals(lastHeader)) {
                        lastHeader = "all";
                    }
                    if (mysar.fileSarList.containsKey(lastHeader + "-file")) {
                        myfilesar = (fileSar) mysar.fileSarList.get(lastHeader + "-file");
                    } else {
                        myfilesar = new fileSar(mysar, lastHeader);
                        DefaultMutableTreeNode myfile = new DefaultMutableTreeNode("file-" + lastHeader);
                        mysar.fileSarList.put(lastHeader + "-file", myfilesar);
                        mysar.pdfList.put(lastHeader + "-file", myfilesar);
                        myfilesar.setGraphLink(lastHeader + "-file");
                        if (mysar.myUI != null) {
                            myfilesar.addtotree(mysar.filetreenode);
                        }
                    }
                    val1 = new Float(matcher.nextToken());
                    val2 = new Float(matcher.nextToken());
                    val3 = new Float(matcher.nextToken());
                    myfilesar.add(now, val1, val2, val3);
                    return 1;
                } else {
                    if ("-".equals(first)) {
                        first = "all";
                    }
                    if (mysar.fileSarList.containsKey(first + "-file")) {
                        myfilesar = (fileSar) mysar.fileSarList.get(first + "-file");
                    } else {
                        myfilesar = new fileSar(mysar, first);
                        DefaultMutableTreeNode myfile = new DefaultMutableTreeNode("file-" + lastHeader);
                        mysar.fileSarList.put(first + "-file", myfilesar);
                        mysar.pdfList.put(first + "-file", myfilesar);
                        myfilesar.setGraphLink(first + "-file");
                        if (mysar.myUI != null) {
                            myfilesar.addtotree(mysar.filetreenode);
                        }
                    }
                    val1 = new Float(lastHeader);
                    val2 = new Float(matcher.nextToken());
                    val3 = new Float(matcher.nextToken());
                    myfilesar.add(now, val1, val2, val3);
                    return 1;
                }
            }

            if ("cpu-msg/s".equals(statType)) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                msgSar mymsgsar;
                if (firstwastime == 1) {
                    if ("-".equals(lastHeader)) {
                        lastHeader = "all";
                    }
                    if (mysar.msgSarList.containsKey(lastHeader + "-msg")) {
                        mymsgsar = (msgSar) mysar.msgSarList.get(lastHeader + "-msg");
                    } else {
                        mymsgsar = new msgSar(mysar, lastHeader);
                        DefaultMutableTreeNode mymsg = new DefaultMutableTreeNode("msg-" + lastHeader);
                        mysar.msgSarList.put(lastHeader + "-msg", mymsgsar);
                        mysar.pdfList.put(lastHeader + "-msg", mymsgsar);
                        mymsgsar.setGraphLink(lastHeader + "-msg");
                        if (mysar.myUI != null) {
                            mymsgsar.addtotree(mysar.msgtreenode);
                        }
                    }
                    val1 = new Float(matcher.nextToken());
                    val2 = new Float(matcher.nextToken());
                    mymsgsar.add(now, val1, val2);
                    return 1;
                } else {
                    if ("-".equals(first)) {
                        first = "all";
                    }
                    if (mysar.msgSarList.containsKey(first + "-msg")) {
                        mymsgsar = (msgSar) mysar.msgSarList.get(first + "-msg");
                    } else {
                        mymsgsar = new msgSar(mysar, first);
                        DefaultMutableTreeNode mymsg = new DefaultMutableTreeNode("msg-" + lastHeader);
                        mysar.msgSarList.put(first + "-msg", mymsgsar);
                        mysar.pdfList.put(first + "-msg", mymsgsar);
                        mymsgsar.setGraphLink(first + "-msg");
                        if (mysar.myUI != null) {
                            mymsgsar.addtotree(mysar.msgtreenode);
                        }
                    }
                    val1 = new Float(lastHeader);
                    val2 = new Float(matcher.nextToken());
                    mymsgsar.add(now, val1, val2);
                    return 1;
                }
            }

            if ("cpu-cswch/s".equals(statType)) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                cswchSar mycswchsar;
                if (firstwastime == 1) {
                    if ("-".equals(lastHeader)) {
                        lastHeader = "all";
                    }
                    if (mysar.cswchSarList.containsKey(lastHeader + "-cswch")) {
                        mycswchsar = (cswchSar) mysar.cswchSarList.get(lastHeader + "-cswch");
                    } else {
                        mycswchsar = new cswchSar(mysar, lastHeader);
                        DefaultMutableTreeNode mycswch = new DefaultMutableTreeNode("cswch-" + lastHeader);
                        mysar.cswchSarList.put(lastHeader + "-cswch", mycswchsar);
                        mysar.pdfList.put(lastHeader + "-cswch", mycswchsar);
                        mycswchsar.setGraphLink(lastHeader + "-cswch");
                        if (mysar.myUI != null) {
                            mycswchsar.addtotree(mysar.cswchtreenode);
                        }
                    }
                    val1 = new Float(matcher.nextToken());
                    mycswchsar.add(now, val1);
                    return 1;
                } else {
                    if ("-".equals(first)) {
                        first = "all";
                    }
                    if ( mysar.cswchSarList.containsKey(first + "-cswch")) {
                        mycswchsar = (cswchSar) mysar.cswchSarList.get(first + "-cswch");
                    } else {
                        mycswchsar = new cswchSar(mysar, first);
                        DefaultMutableTreeNode mycswch = new DefaultMutableTreeNode("cswch-" + lastHeader);
                        mysar.cswchSarList.put(first + "-cswch", mycswchsar);
                        mysar.pdfList.put(first + "-cswch", mycswchsar);
                        mycswchsar.setGraphLink(first + "-cswch");
                        if (mysar.myUI != null) {
                            mycswchsar.addtotree(mysar.cswchtreenode);
                        }
                    }
                    val1 = new Float(lastHeader);
                    mycswchsar.add(now, val1);
                    return 1;
                }
            }

            if ("cpu-scall/s".equals(statType)) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                syscallSar myscallsar;
                if (firstwastime == 1) {
                    if ("-".equals(lastHeader)) {
                        lastHeader = "all";
                    }
                    if (mysar.scallSarList.containsKey(lastHeader + "-scall")) {
                        myscallsar = (syscallSar) mysar.scallSarList.get(lastHeader + "-scall");
                    } else {
                        myscallsar = new syscallSar(mysar, lastHeader);
                        DefaultMutableTreeNode myscall = new DefaultMutableTreeNode("scall-" + lastHeader);
                        mysar.scallSarList.put(lastHeader + "-scall", myscallsar);
                        mysar.pdfList.put(lastHeader + "-scall", myscallsar);
                        myscallsar.setGraphLink(lastHeader + "-scall");
                        if (mysar.myUI != null) {
                            myscallsar.addtotree(mysar.scalltreenode);
                        }
                    }
                    val1 = new Float(matcher.nextToken());
                    val2 = new Float(matcher.nextToken());
                    val3 = new Float(matcher.nextToken());
                    val4 = new Float(matcher.nextToken());
                    val5 = new Float(matcher.nextToken());
                    val6 = new Float(matcher.nextToken());
                    val7 = new Float(matcher.nextToken());
                    myscallsar.add(now, val1, val2, val3, val4, val5, val6, val7);
                    return 1;
                } else {
                    if ("-".equals(first)) {
                        first = "all";
                    }
                    if (mysar.scallSarList.containsKey(first + "-scall")) {
                        myscallsar = (syscallSar) mysar.scallSarList.get(first + "-scall");
                    } else {
                        myscallsar = new syscallSar(mysar, first);
                        DefaultMutableTreeNode myscall = new DefaultMutableTreeNode("scall-" + lastHeader);
                        mysar.scallSarList.put(first + "-scall", myscallsar);
                        mysar.pdfList.put(first + "-scall", myscallsar);
                        myscallsar.setGraphLink(first + "-scall");
                        if (mysar.myUI != null) {
                            myscallsar.addtotree(mysar.scalltreenode);
                        }
                    }
                    val1 = new Float(lastHeader);
                    val2 = new Float(matcher.nextToken());
                    val3 = new Float(matcher.nextToken());
                    val4 = new Float(matcher.nextToken());
                    val5 = new Float(matcher.nextToken());
                    val6 = new Float(matcher.nextToken());
                    val7 = new Float(matcher.nextToken());
                    myscallsar.add(now, val1, val2, val3, val4, val5, val6, val7);
                    return 1;
                }
            }

            if ("device".equals(statType) || "Kbs/s".equals(statType)) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                diskxferSar mydiskxfer;
                diskwaitSar mydiskwait;
                if (firstwastime == 1) {
                    mydiskxfer = (diskxferSar) mysar.disksSarList.get(lastHeader + "Aixxfer");                    
                    if (mydiskxfer == null) {
                        diskName tmp = new diskName(lastHeader);
                        mysar.AlternateDiskName.put(lastHeader, tmp);
                        tmp.setTitle((String) mysar.Adiskname.get(lastHeader));
                        mydiskxfer = new diskxferSar(mysar, lastHeader);
                        mydiskxfer.setdiskOpt(statType);
                        mysar.disksSarList.put(lastHeader + "Aixxfer", mydiskxfer);
                        mydiskwait = new diskwaitSar(mysar, lastHeader);
                        mysar.disksSarList.put(lastHeader + "Aixwait", mydiskwait);
                        DefaultMutableTreeNode mydisk = new DefaultMutableTreeNode(tmp.showTitle());
                        mydisk.add(new DefaultMutableTreeNode(new GraphDescription(mydiskxfer, "DISKXFER", "Disk Xfer", null)));
                        mysar.pdfList.put(lastHeader + "Aixxfer", mydiskxfer);
                        mydiskxfer.setGraphLink(lastHeader + "Aixxfer");
                        mydisk.add(new DefaultMutableTreeNode(new GraphDescription(mydiskwait, "DISKWAIT", "Disk Wait", null)));
                        mysar.pdfList.put(lastHeader + "Aixwait", mydiskwait);
                        mydiskwait.setGraphLink(lastHeader + "Aixwait");
                        mysar.diskstreenode.add(mydisk);
                    } else {
                        mydiskwait = (diskwaitSar) mysar.disksSarList.get(lastHeader + "Aixwait");
                    }
                    val1 = new Float(matcher.nextToken());
                    val2 = new Float(matcher.nextToken());
                    val3 = new Float(matcher.nextToken());
                    val4 = new Float(matcher.nextToken());
                    val5 = new Float(matcher.nextToken());
                    val6 = new Float(matcher.nextToken());
                    mydiskxfer.add(now, val4, val3, val6);
                    mydiskwait.add(now, val2, val5, val1);
                    return 1;
                } else {
                    mydiskxfer = (diskxferSar) mysar.disksSarList.get(first + "Aixxfer");
                    if ( mydiskxfer == null) {    
                        diskName tmp = new diskName(first);
                        mysar.AlternateDiskName.put(first, tmp);
                        tmp.setTitle((String) mysar.Adiskname.get(first));
                        mydiskxfer = new diskxferSar(mysar, first);
                        mydiskxfer.setdiskOpt(statType);
                        mysar.disksSarList.put(first + "Aixxfer", mydiskxfer);
                        mydiskwait = new diskwaitSar(mysar, first);
                        mysar.disksSarList.put(first + "Aixwait", mydiskwait);
                        DefaultMutableTreeNode mydisk = new DefaultMutableTreeNode(tmp.showTitle());
                        mydisk.add(new DefaultMutableTreeNode(new GraphDescription(mydiskxfer, "DISKXFER", "Disk Xfer", null)));
                        mysar.pdfList.put(first + "Aixxfer", mydiskxfer);
                        mydiskxfer.setGraphLink(first + "Aixxfer");
                        mydisk.add(new DefaultMutableTreeNode(new GraphDescription(mydiskwait, "DISKWAIT", "Disk Wait", null)));
                        mysar.pdfList.put(first + "Aixwait", mydiskwait);
                        mydiskwait.setGraphLink(first + "Aixwait");
                        mysar.diskstreenode.add(mydisk);
                    } else {
                       mydiskwait = (diskwaitSar) mysar.disksSarList.get(first + "Aixwait"); 
                    }
                    val1 = new Float(lastHeader);
                    val2 = new Float(matcher.nextToken());
                    val3 = new Float(matcher.nextToken());
                    val4 = new Float(matcher.nextToken());
                    val5 = new Float(matcher.nextToken());
                    val6 = new Float(matcher.nextToken());
                    mydiskxfer.add(now, val4, val3, val6);
                    mydiskwait.add(now, val2, val5, val1);
                    return 1;
                }
            }
            if ("ksched/s".equals(statType)) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                sarKERNEL3.add(now, val1, val2, val3);
                return 1;
            }
        } catch (SeriesException e) {
            System.out.println("Aix parser: " + e);
            return -1;
        }
        return 0;
    }
    private final kSar mysar;
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
    int entreetype;
    int firstwastime;

    // Aix
    cpuSar sarCPU3 = null;
    bufferSar sarBUFFER3 = null;
    squeueSar sarSQUEUE3 = null;
    rqueueSar sarRQUEUE3 = null;
    ttySar sarTTY3 = null;
    syscallSar sarSYSCALL3 = null;
    msgSar sarMSG3 = null;
    cswchSar sarCSWCH3 = null;
    fileSar sarFILE3 = null;
    kernelSar sarKERNEL3 = null;
    slotSar sarSLOT3 = null;
}
