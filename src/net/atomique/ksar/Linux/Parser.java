/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.diskName;
import net.atomique.ksar.kSar;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Second;

/**
 *
 * @author alex
 */
public class Parser {

    public Parser(kSar hissar) {
        mysar = hissar;
    }

    public int parse(String thisLine, String first, StringTokenizer matcher) {
        String prog;
        val1 = null;
        val2 = null;
        val3 = null;
        val4 = null;
        val5 = null;
        val5 = null;
        val6 = null;
        val7 = null;
        val8 = null;
        val9 = null;
        val10 = null;
        val11 = null;
        int headerFound = 0;

        if (thisLine.indexOf("proc/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("cswch/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("%user") > 0 || thisLine.indexOf("%usr") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
            lastHeader = matcher.nextToken();
        }
        if (thisLine.indexOf("intr/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
            lastHeader = matcher.nextToken();
        }
        if (thisLine.indexOf("CPU") > 0 && (thisLine.indexOf("%user") <= 0 || thisLine.indexOf("%usr") <= 0)) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("pswpin/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("bread/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("blks/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("sect") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("bufpg/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("rxpck/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("rxerr/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("rd_sec/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("pgpgout/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("kbmemused") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("kbswpused") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("file-sz/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("tcpsck") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("runq-sz") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("dentunusd") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("retrans/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("scall/s") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("plist-sz") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("TTY") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }
        if (thisLine.indexOf("PID") > 0) {
            headerFound = 1;
            mysar.underaverage = 0;
        }

        // parse time or continue except device line that are missing the time entry
        String[] sarTime = first.split(":");
        if (sarTime.length != 3) {
            return 1;
        } else {
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
        }


        lastHeader = new String(matcher.nextToken());
        // was a header ?
        if (headerFound == 1) {
            if (lastHeader.equals(statType)) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("pgpgin/s") && thisLine.indexOf("activepg") > 0 && statType.equals("activepg")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("pgpgin/s") && thisLine.indexOf("pgscank/s") > 0 && statType.equals("pgscank/s")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("dentunusd") && thisLine.indexOf("%file-sz") > 0 && statType.equals("%file-sz")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("kbmemfree") && thisLine.indexOf("kbmemshrd") > 0 && statType.equals("kbmemshrd")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("kbmemfree") && thisLine.indexOf("%commit") > 0 && statType.equals("newkmem")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("%user") && thisLine.indexOf("%iowait") > 0 && statType.equals("%iowait")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("%user") && thisLine.indexOf("%guest") > 0 && statType.equals("%guest")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("%ser") && thisLine.indexOf("%steal") > 0 && statType.equals("%steal")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("DEV") && thisLine.indexOf("rd_sec/s") > 0 && statType.equals("rd_sec/s")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("IFACE") && thisLine.indexOf("rxpck/s") > 0 && statType.equals("rxpck/s")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("IFACE") && thisLine.indexOf("rxerr/s") > 0 && statType.equals("rxerr/s")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("DEV") && thisLine.indexOf("avgrq-sz") > 0 && statType.equals("avgrq-sz")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("runq-sz") && thisLine.indexOf("ldavg-15") > 0 && statType.equals("ldavg-15")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("bufpg/s") && thisLine.indexOf("shmpg/s") > 0 && statType.equals("shmpg/s")) {
                headerFound = 0;
                return 1;
            }
            //
            // NOT BUT PIDSTAT
            //
            if (lastHeader.equals("PID") && thisLine.indexOf("RSS") > 0 && statType.equals("RSS")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("PID") && thisLine.indexOf("kB_ccwr") > 0 && statType.equals("kB_ccwr")) {
                headerFound = 0;
                return 1;
            }
            if (lastHeader.equals("%user") && thisLine.indexOf("Command") > 0 && statType.equals("Command")) {
                headerFound = 0;
                return 1;
            }

            statType = lastHeader;
            if (statType.equals("%user") && thisLine.indexOf("Command") < 0) {
                if (thisLine.indexOf("%iowait") > 0) {
                    statType = "%iowait";
                }
                if (thisLine.indexOf("%steal") > 0) {
                    statType = "%steal";
                }

                if (!mysar.hascpunode) {
                    if (mysar.myUI != null) {
                        mysar.add2tree(mysar.graphtree, mysar.cpustreenode);
                    }
                    mysar.hascpunode = true;
                }
                return 1;
            }

            if (statType.equals("%usr") && thisLine.indexOf("Command") < 0) {
                if (!mysar.hascpunode) {
                    if (mysar.myUI != null) {
                        mysar.add2tree(mysar.graphtree, mysar.cpustreenode);
                    }
                    mysar.hascpunode = true;
                }
                return 1;
            }
            if (thisLine.indexOf("RSS") >= 0) {
                statType = "RSS";
                if (!mysar.haspidnode) {
                    if (mysar.myUI != null) {
                        mysar.add2tree(mysar.graphtree, mysar.pidstreenode);
                    }
                    mysar.haspidnode = true;
                }
                return 1;
            }

            if (thisLine.indexOf("kB_ccwr") >= 0) {
                statType = "kB_ccwr";
                if (!mysar.haspidnode) {
                    if (mysar.myUI != null) {
                        mysar.add2tree(mysar.graphtree, mysar.pidstreenode);
                    }
                    mysar.haspidnode = true;
                }
                return 1;
            }
            if (statType.equals("%user") && thisLine.indexOf("Command") >= 0) {

                if (thisLine.indexOf("Command") >= 0) {
                    statType = "Command";
                }
                if (!mysar.haspidnode) {
                    if (mysar.myUI != null) {
                        mysar.add2tree(mysar.graphtree, mysar.pidstreenode);
                    }
                    mysar.haspidnode = true;
                }
                return 1;

            }
            //

            //
            if (statType.equals("proc/s")) {
                if (sarPROC == null) {
                    sarPROC = new procSar(mysar);
                    if (mysar.myUI != null) {
                        sarPROC.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxprocSar", sarPROC);
                    sarPROC.setGraphLink("LinuxprocSar");
                }
                if (thisLine.indexOf("cswch/s") >= 0) {
                    if (sarCSWCH == null) {
                        sarCSWCH = new cswchSar(mysar);
                        if (mysar.myUI != null) {
                            sarCSWCH.addtotree(mysar.graphtree);
                        }
                        mysar.pdfList.put("LinuxcswchSar", sarCSWCH);
                        sarCSWCH.setGraphLink("LinuxcswchSar");
                    }
                    statType = "proccswch";
                    return 1;
                } else {
                    statType = "proc/s";
                    return 1;
                }
            }

            if (statType.equals("cswch/s")) {
                if (sarCSWCH == null) {
                    sarCSWCH = new cswchSar(mysar);
                    if (mysar.myUI != null) {
                        sarCSWCH.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxcswchSar", sarCSWCH);
                    sarCSWCH.setGraphLink("LinuxcswchSar");
                }
                return 1;
            }
            if (statType.equals("intr/s")) {
                if (sarINTR == null) {
                    sarINTR = new intrSar(mysar);
                    if (mysar.myUI != null) {
                        sarINTR.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxintrSar", sarINTR);
                    sarINTR.setGraphLink("LinuxintrSar");
                }
                return 1;
            }
            if (statType.equals("CPU")) {
                String intrheader = null;
                try {
                    while ((intrheader = matcher.nextToken()) != null) {
                        String intrname = intrheader.replaceFirst("/s", "");
                        intrlist.add(intrname);
                        intrlistSar mysarintr = (intrlistSar) mysar.intrSarlist.get(intrname + "-intr");
                        if (mysarintr == null) {
                            mysarintr = new intrlistSar(mysar, intrname);
                            mysar.intrSarlist.put(intrname + "-intr", mysarintr);
                            mysar.pdfList.put(intrname + "-intr", mysarintr);
                            mysarintr.setGraphLink("intr-" + intrname);
                            if (mysar.myUI != null) {
                                if (!mysar.hasintrlistnode) {
                                    mysar.hasintrlistnode = true;
                                    mysar.add2tree(mysar.graphtree, mysar.intrtreenode);
                                }
                                mysarintr.addtotree(mysar.intrtreenode);
                            }
                        }
                    }
                } catch (NoSuchElementException ee) {
                }

                return 1;
            }
            if (statType.equals("pswpin/s")) {
                if (sarSWAP2 == null) {
                    sarSWAP2 = new swapSar(mysar);
                    if (mysar.myUI != null) {
                        sarSWAP2.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxswapSar", sarSWAP2);
                    sarSWAP2.setGraphLink("LinuxswapSar");
                }
                return 1;
            }
            if (statType.equals("tps")) {
                if (sarIO == null) {
                    sarIO = new ioSar(mysar);
                    if (mysar.myUI != null) {
                        sarIO.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxioSar", sarIO);
                    sarIO.setGraphLink("LinuxioSar");
                }
                return 1;
            }

            if (statType.equals("DEV")) {
                if (thisLine.indexOf("rd_sec/s") > 0) {
                    statType = "rd_sec/s";
                }
                if (thisLine.indexOf("avgrq-sz") > 0) {
                    statType = "avgrq-sz";
                }
                if (!mysar.hasdisknode) {
                    if (mysar.myUI != null) {
                        mysar.add2tree(mysar.graphtree, mysar.diskstreenode);
                    }
                    mysar.hasdisknode = true;
                }
                return 1;
            }

            if (statType.equals("IFACE")) {
                if (thisLine.indexOf("rxpck/s") > 0) {
                    statType = "rxpck/s";
                }
                if (thisLine.indexOf("rxerr/s") > 0) {
                    statType = "rxerr/s";
                }
                if (!mysar.hasifnode) {
                    if (mysar.myUI != null) {
                        mysar.add2tree(mysar.graphtree, mysar.ifacetreenode);
                    }
                    mysar.hasifnode = true;
                }
                return 1;
            }

            if (statType.equals("runq-sz")) {
                if (sarLOAD == null) {
                    sarLOAD = new loadSar(mysar);
                    if (mysar.myUI != null) {
                        sarLOAD.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxloadSar", sarLOAD);
                    sarLOAD.setGraphLink("LinuxloadSar");
                }
                if (thisLine.indexOf("ldavg-15") > 0) {
                    statType = "ldavg-15";
                    sarLOAD.setloadOpt("ldavg-15");
                }
                return 1;
            }

            if (statType.equals("frmpg/s")) {
                if (sarPAGE == null) {
                    sarPAGE = new pageSar(mysar);
                    if (mysar.myUI != null) {
                        sarPAGE.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxpageSar", sarPAGE);
                    sarPAGE.setGraphLink("LinuxpageSar");
                }
                if (thisLine.indexOf("shmpg/s") > 0) {
                    statType = "shmpg/s";
                    sarPAGE.setpageOpt("shmpg/s");
                }
                return 1;
            }
            if (statType.equals("totsck")) {
                if (sarSOCK == null) {
                    sarSOCK = new sockSar(mysar);
                    if (mysar.myUI != null) {
                        sarSOCK.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxsockSar", sarSOCK);
                    sarSOCK.setGraphLink("LinuxsockSar");
                }
                return 1;
            }
            if (statType.equals("kbmemfree") && thisLine.indexOf("%commit") < 0) {
                if (sarKBMEM == null) {
                    sarKBMEM = new kbmemSar(mysar);
                    if (mysar.myUI != null) {
                        sarKBMEM.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxkbmemSar", sarKBMEM);
                    sarKBMEM.setGraphLink("LinuxkbmemSar");
                }

                if (sarKBSWP == null) {
                    sarKBSWP = new kbswpSar(mysar);
                    if (mysar.myUI != null) {
                        sarKBSWP.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxkbswpSar", sarKBSWP);
                    sarKBSWP.setGraphLink("LinuxkbswpSar");
                }

                if (sarKBMISC == null) {
                    sarKBMISC = new kbmiscSar(mysar);
                    if (mysar.myUI != null) {
                        sarKBMISC.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxkbmiscSar", sarKBMISC);
                    sarKBMISC.setGraphLink("LinuxkbmiscSar");
                }

                if (thisLine.indexOf("kbmemshrd") > 0) {
                    sarKBSWP.setswpOpt("kbmemshrd");
                    sarKBMISC.setmiscOpt("kbmemshrd");
                    statType = "kbmemshrd";
                }
                return 1;
            }
            if (statType.equals("kbswpfree")) {
                if (sarKBSWP == null) {
                    sarKBSWP = new kbswpSar(mysar);
                    if (mysar.myUI != null) {
                        sarKBSWP.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxkbswpSar", sarKBSWP);
                    sarKBSWP.setGraphLink("LinuxkbswpSar");
                }

                return 1;
            }
            if (statType.equals("kbmemfree") && thisLine.indexOf("%commit") >= 0) {
                if (sarKBMEM == null) {
                    sarKBMEM = new kbmemSar(mysar);
                    if (mysar.myUI != null) {
                        sarKBMEM.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxkbmemSar", sarKBMEM);
                    sarKBMEM.setGraphLink("LinuxkbmemSar");
                }

                if (sarKBMISC == null) {
                    sarKBMISC = new kbmiscSar(mysar);
                    if (mysar.myUI != null) {
                        sarKBMISC.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxkbmiscSar", sarKBMISC);
                    sarKBMISC.setGraphLink("LinuxkbmiscSar");
                }
                statType = "newkmem";
                // commit %commit
                return 1;
            }

            if (statType.equals("pgpgin/s")) {
                if (sarPGP == null) {
                    sarPGP = new pgpSar(mysar);
                    if (mysar.myUI != null) {
                        sarPGP.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxpgpSar", sarPGP);
                    sarPGP.setGraphLink("LinuxpgpSar");
                }
                if (thisLine.indexOf("activepg") > 0) {
                    sarPGP.setpgpOpt("activepg");
                    statType = "activepg";
                }
                if (thisLine.indexOf("pgscank/s") > 0) {
                    sarPGP.setpgpOpt("pgscank/s");
                    statType = "pgscank/s";
                }

                return 1;
            }
            if (statType.equals("call/s")) {
                if (sarNFS == null) {
                    sarNFS = new nfsSar(mysar);
                    if (mysar.myUI != null) {
                        sarNFS.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxnfsSar", sarNFS);
                    sarNFS.setGraphLink("LinuxnfsSar");
                }
                return 1;
            }
            if (statType.equals("scall/s")) {
                if (sarNFSD == null) {
                    sarNFSD = new nfsdSar(mysar);
                    if (mysar.myUI != null) {
                        sarNFSD.addtotree(mysar.graphtree);
                    }
                    mysar.pdfList.put("LinuxnfsdSar", sarNFSD);
                    sarNFSD.setGraphLink("LinuxnfsdSar");
                }
            }
            if (statType.equals("dentunusd")) {
                if (thisLine.indexOf("%file-sz") > 0) {
                    statType = "%file-sz";
                }
                return 1;
            }
            if (statType.equals("TTY")) {
                return 1;
            }

            headerFound = 0;
            return 1;
        }

        try {
            if (thisLine.indexOf("nan") > 0) {
                return 1;
            }
            if (statType.equals("CPU")) {
                String intrname = null;
                String cpu = lastHeader;
                int i = 0;
                String intr;
                try {
                    while ((intr = matcher.nextToken()) != null) {
                        intrname = (String) intrlist.get(i);
                        intrlistSar intrList = (intrlistSar) mysar.intrSarlist.get(intrname + "-intr");
                        if (intrList != null) {
                            intrList.add(now, "CPU " + cpu, new Float(intr));
                        }
                        i++;
                    }
                } catch (NoSuchElementException ee) {
                    return 1;
                }
            }
            if (statType.equals("proc/s")) {
                // proc/s
                val1 = new Float(lastHeader);
                sarPROC.add(now, val1);
                return 1;
            }
            if (statType.equals("proccswch")) {
                // proc/s
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                sarPROC.add(now, val1);
                sarCSWCH.add(now, val2);
                return 1;
            }

            if (statType.equals("cswch/s")) {
                // proc/s
                val1 = new Float(lastHeader);
                sarCSWCH.add(now, val1);
                return 1;
            }
            //
            // 
            // PIDSTAT Command
            if (statType.equals("Command")) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                prog = matcher.nextToken();
                PidCpuSar tmppidcpu = (PidCpuSar) mysar.pidSarList.get(lastHeader + "-pidcpu");
                if (tmppidcpu == null) {
                    tmppidcpu = new PidCpuSar(mysar, prog, lastHeader);
                    DefaultMutableTreeNode mypid = new DefaultMutableTreeNode("pid-" + lastHeader);
                    mysar.pidSarList.put(lastHeader + "-pidcpu", tmppidcpu);
                    mysar.pdfList.put(lastHeader + "-pidcpu", tmppidcpu);
                    tmppidcpu.setGraphLink(lastHeader + "-pidcpu");
                    if (mysar.myUI != null) {
                        tmppidcpu.addtotree(mysar.pidstreenode);
                    }
                }
                tmppidcpu.add(now, val1, val2, val3);

                return 1;
            }
            //
            if (statType.equals("RSS")) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());

                prog = matcher.nextToken();
                PidMemSar tmppidmem = (PidMemSar) mysar.pidSarList.get(lastHeader + "-pidmem");
                if (tmppidmem == null) {
                    tmppidmem = new PidMemSar(mysar, prog, lastHeader);
                    DefaultMutableTreeNode mypid = new DefaultMutableTreeNode("pid-" + lastHeader);
                    mysar.pidSarList.put(lastHeader + "-pidmem", tmppidmem);
                    mysar.pdfList.put(lastHeader + "-pidmem", tmppidmem);
                    tmppidmem.setGraphLink(lastHeader + "-pidmem");
                    if (mysar.myUI != null) {
                        tmppidmem.addtotree(mysar.pidstreenode);
                    }
                }

                tmppidmem.add(now, val1, val2, val3, val4, val5);

                return 1;
            }
            // kB_ccwr
            if (statType.equals("kB_ccwr")) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());

                prog = matcher.nextToken();
                PidIOSar tmppidio = (PidIOSar) mysar.pidSarList.get(lastHeader + "-pidkb");
                if (tmppidio == null) {
                    tmppidio = new PidIOSar(mysar, prog, lastHeader);
                    DefaultMutableTreeNode mypid = new DefaultMutableTreeNode("pid-" + lastHeader + "/" + prog);
                    mysar.pidSarList.put(lastHeader + "-pidkb", tmppidio);
                    mysar.pdfList.put(lastHeader + "-pidkb", tmppidio);
                    tmppidio.setGraphLink(lastHeader + "-pidkb");
                    if (mysar.myUI != null) {
                        tmppidio.addtotree(mysar.pidstreenode);
                    }
                } else {
                    tmppidio = (PidIOSar) mysar.pidSarList.get(prog + "/" + lastHeader + "-pidkb");
                }

                tmppidio.add(now, val1, val2, val3);

                return 1;
            }
            //
            //
            if (statType.equals("%user")) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                cpuSar mysarcpu = (cpuSar) mysar.cpuSarList.get(lastHeader + "-cpu");
                if (mysarcpu == null) {
                    mysarcpu = new cpuSar(mysar, lastHeader);
                    mysarcpu.setcpuOpt(statType);
                    DefaultMutableTreeNode mycpu = new DefaultMutableTreeNode("cpu-" + lastHeader);
                    mysar.cpuSarList.put(lastHeader + "-cpu", mysarcpu);
                    mysar.pdfList.put(lastHeader + "-cpu", mysarcpu);
                    mysarcpu.setGraphLink("cpu-" + lastHeader);
                    if (mysar.myUI != null) {
                        mysarcpu.addtotree(mysar.cpustreenode);
                    }
                }
                // %user	 %nice	 %system   %idle
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                mysarcpu.add(now, val1, val2, val3, val4);
                return 1;
            }

            if (statType.equals("%iowait")) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                cpuSar mysarcpu = (cpuSar) mysar.cpuSarList.get(lastHeader + "-cpu");
                if (mysarcpu == null) {
                    mysarcpu = new cpuSar(mysar, lastHeader);
                    mysarcpu.setcpuOpt(statType);
                    DefaultMutableTreeNode mycpu = new DefaultMutableTreeNode("cpu-" + lastHeader);
                    mysar.cpuSarList.put(lastHeader + "-cpu", mysarcpu);
                    mysar.pdfList.put(lastHeader + "-cpu", mysarcpu);
                    mysarcpu.setGraphLink("cpu-" + lastHeader);
                    if (mysar.myUI != null) {
                        mysarcpu.addtotree(mysar.cpustreenode);
                    }
                }
                // %user	 %nice	 %system   %iowait	   %idle
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                mysarcpu.add(now, val1, val2, val3, val4, val5);
                return 1;
            }
            if (statType.equals("%steal")) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                cpuSar mysarcpu = (cpuSar) mysar.cpuSarList.get(lastHeader + "-cpu");
                if (mysarcpu == null) {
                    mysarcpu = new cpuSar(mysar, lastHeader);
                    mysarcpu.setcpuOpt(statType);
                    DefaultMutableTreeNode mycpu = new DefaultMutableTreeNode("cpu-" + lastHeader);
                    mysar.cpuSarList.put(lastHeader + "-cpu", mysarcpu);
                    mysar.pdfList.put(lastHeader + "-cpu", mysarcpu);
                    mysarcpu.setGraphLink("cpu-" + lastHeader);
                    if (mysar.myUI != null) {
                        mysarcpu.addtotree(mysar.cpustreenode);
                    }
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                mysarcpu.add(now, val1, val2, val3, val4, val5, val6);
                return 1;
            }
            //  CPU      %usr %nice %sys %iowait %steal %irq %soft %guest %idle
            if (statType.equals("%usr")) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                cpuSar mysarcpu = (cpuSar) mysar.cpuSarList.get(lastHeader + "-cpu");
                if (mysarcpu == null) {
                    mysarcpu = new cpuSar(mysar, lastHeader);
                    mysarcpu.setcpuOpt(statType);
                    DefaultMutableTreeNode mycpu = new DefaultMutableTreeNode("cpu-" + lastHeader);
                    mysar.cpuSarList.put(lastHeader + "-cpu", mysarcpu);
                    mysar.pdfList.put(lastHeader + "-cpu", mysarcpu);
                    mysarcpu.setGraphLink("cpu-" + lastHeader);
                    if (mysar.myUI != null) {
                        mysarcpu.addtotree(mysar.cpustreenode);
                    }
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                mysarcpu.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9);
                return 1;
            }
            if (statType.equals("intr/s")) {
                // proc/s
                if (lastHeader.equals("sum")) {
                    lastHeader = new String(matcher.nextToken());
                    val1 = new Float(lastHeader);
                    sarINTR.add(now, val1);
                }
                return 1;
            }
            if (statType.equals("pswpin/s")) {
                // proc/s
                val1 = new Float(lastHeader);
                val2 = new Float(new String(matcher.nextToken()));
                sarSWAP2.add(now, val1, val2);
                return 1;
            }
            if (statType.equals("tps")) {
                // tps
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                sarIO.add(now, val1, val2, val3, val4, val5);
                return 1;
            }

            if (statType.equals("avgrq-sz")) { //DEV		 tps  rd_sec/s	wr_sec/s  avgrq-sz	 avgqu-sz	  await		svctm	  %util
                if (mysar.underaverage == 1) {
                    return 1;
                }
                blockSar mysarblock = (blockSar) mysar.disksSarList.get(lastHeader + "-t1");
                block2Sar mysarblock2;
                if (mysarblock == null) {
                    diskName tmp = new diskName(lastHeader);
                    mysar.AlternateDiskName.put(lastHeader, tmp);
                    tmp.setTitle((String) mysar.Adiskname.get(lastHeader));
                    mysarblock = new blockSar(mysar, lastHeader, tmp);
                    mysarblock2 = new block2Sar(mysar, lastHeader, tmp);
                    mysarblock.setioOpt("avgrq-sz");
                    DefaultMutableTreeNode mydisk = new DefaultMutableTreeNode(tmp.showTitle());
                    if (mysar.myUI != null) {
                        mysarblock.addtotree(mydisk);
                        mysarblock2.addtotree(mydisk);
                    }
                    mysar.disksSarList.put(lastHeader + "-t1", mysarblock);
                    mysarblock.setGraphLink(lastHeader + "-t1");
                    mysar.disksSarList.put(lastHeader + "-t2", mysarblock2);
                    mysarblock2.setGraphLink(lastHeader + "-t2");
                    mysar.pdfList.put(lastHeader + "-t1", mysarblock);
                    mysar.pdfList.put(lastHeader + "-t2", mysarblock2);
                    mysar.diskstreenode.add(mydisk);
                } else {
                    mysarblock2 = (block2Sar) mysar.disksSarList.get(lastHeader + "-t2");
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                mysarblock.add(now, val1, val2, val3);
                mysarblock2.add(now, val4, val5, val6, val7, val8);
                return 1;
            }
            if (statType.equals("rd_sec/s")) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                blockSar mysarblock = (blockSar) mysar.disksSarList.get(lastHeader + "-t1");
                if (mysarblock == null) {
                    diskName tmp = new diskName(lastHeader);
                    mysar.AlternateDiskName.put(lastHeader, tmp);
                    tmp.setTitle((String) mysar.Adiskname.get(lastHeader));
                    mysarblock = new blockSar(mysar, lastHeader, tmp);
                    DefaultMutableTreeNode mydisk = new DefaultMutableTreeNode(tmp.showTitle());
                    mysar.disksSarList.put(lastHeader + "-t1", mysarblock);
                    mysar.pdfList.put(lastHeader + "-t1", mysarblock);
                    mysarblock.setGraphLink(lastHeader + "-t1");
                    mysarblock.setioOpt("rd_sec/s");
                    if (mysar.myUI != null) {
                        mysarblock.addtotree(mydisk);
                        mysar.diskstreenode.add(mydisk);
                    }
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                mysarblock.add(now, val1, val2, val3);
                return 1;
            }
            if (statType.equals("DEV")) {
                if (mysar.underaverage == 1) {
                    return 1;
                }
                blockSar mysarblock = (blockSar) mysar.disksSarList.get(lastHeader + "-t1");
                if (mysarblock == null) {
                    diskName tmp = new diskName(lastHeader);
                    mysar.AlternateDiskName.put(lastHeader, tmp);
                    tmp.setTitle((String) mysar.Adiskname.get(lastHeader));
                    mysarblock = new blockSar(mysar, lastHeader, tmp);
                    DefaultMutableTreeNode mydisk = new DefaultMutableTreeNode(tmp.showTitle());
                    mysar.disksSarList.put(lastHeader + "-t1", mysarblock);
                    mysarblock.setGraphLink(lastHeader + "-t1");
                    mysar.pdfList.put(lastHeader + "-t1", mysarblock);
                    if (mysar.myUI != null) {
                        mysarblock.addtotree(mydisk);
                        mysar.diskstreenode.add(mydisk);
                    }
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                mysarblock.add(now, val1, val2);
                return 1;
            }
            if (statType.equals("ldavg-15")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                sarLOAD.add(now, val1, val2, val3, val4, val5);
                return 1;
            }
            if (statType.equals("runq-sz")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                sarLOAD.add(now, val1, val2, val3, val4);
                return 1;
            }
            if (statType.equals("frmpg/s")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                sarPAGE.add(now, val1, val2, val3);
                return 1;
            }
            if (statType.equals("shmpg/s")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                sarPAGE.add(now, val1, val2, val3, val4);
                return 1;
            }
            if (statType.equals("totsck")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                sarSOCK.add(now, val1, val2, val3, val4, val5);
                return 1;
            }
            if (statType.equals("newkmem")) { // kbmemfree kbmemused  %memused kbbuffers  kbcached  kbcommit   %commit
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                sarKBMEM.add(now, val1, val2, val3);
                sarKBMISC.add(now, val4, val5);
                sarKBMISC.addused_bufferadj(now, new Float(val2.floatValue() - val4.floatValue() - val5.floatValue()));
                return 1;
            }
            if (statType.equals("kbmemfree")) { //kbmemfree kbmemused	 %memused kbbuffers	 kbcached kbswpfree kbswpused  %swpused	 kbswpcad
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                sarKBMEM.add(now, val1, val2, val3, val4, val5, val7);
                sarKBMISC.add(now, val4, val5);
                sarKBMISC.addused_bufferadj(now, new Float(val2.floatValue() - val4.floatValue() - val5.floatValue()));
                sarKBSWP.add(now, val6, val7, val8, val9);
                return 1;
            }
            if (statType.equals("kbmemshrd")) { //kbmemfree kbmemused	 %memused kbmemshrd kbbuffers  kbcached kbswpfree kbswpused	 %swpused
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                sarKBMEM.add(now, val1, val2, val3, val4, val5, val7);
                sarKBMISC.add(now, val4, val5, val6);
                sarKBMISC.addused_bufferadj(now, new Float(val2.floatValue() - val4.floatValue() - val5.floatValue()));
                sarKBSWP.add(now, val7, val8, val9);
                return 1;
            }
            if (statType.equals("kbswpfree")) { //kbswpfree kbswpused  %swpused  kbswpcad   %swpcad
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                sarKBSWP.add(now, val1, val2, val3, val4, val5);
                return 1;
            }
            if (statType.equals("pgpgin/s")) { // pgpgin/s pgpgout/s	 fault/s  majflt/s
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                sarPGP.add(now, val1, val2, val3, val4);
                return 1;
            }

            if (statType.equals("activepg")) { // pgpgin/s pgpgout/s	activepg  inadtypg	inaclnpg  inatarpg
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                sarPGP.add(now, val1, val2, val3, val4, val5, val6);
                return 1;
            }
            if (statType.equals("pgscank/s")) {  //pgpgin/s pgpgout/s   fault/s  majflt/s  pgfree/s pgscank/s pgscand/s pgsteal/s    %vmeff
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                sarPGP.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9);
                return 1;
            }

            if (statType.equals("call/s")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                sarNFS.add(now, val1, val2, val3, val4, val5, val6);
                return 1;
            }
            if (statType.equals("scall/s")) {
                val1 = new Float(lastHeader);
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                val10 = new Float(matcher.nextToken());
                val11 = new Float(matcher.nextToken());
                sarNFSD.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9, val10, val11);
                return 1;
            }
            if (statType.equals("rxpck/s")) { //DEV		tps	 rd_sec/s  wr_sec/s	 avgrq-sz	 avgqu-sz	  await		svctm	  %util
                if (mysar.underaverage == 1) {
                    return 1;
                }
                iface1Sar mysarif1 = (iface1Sar) mysar.ifaceSarList.get(lastHeader + "-if1");
                iface2Sar mysarif2;
                if (mysarif1 == null) {
                    mysarif1 = new iface1Sar(mysar, lastHeader);
                    mysarif2 = new iface2Sar(mysar, lastHeader);
                    DefaultMutableTreeNode myif = new DefaultMutableTreeNode(lastHeader);
                    if (mysar.myUI != null) {
                        mysarif1.addtotree(myif);
                        mysarif2.addtotree(myif);
                    }
                    mysar.ifaceSarList.put(lastHeader + "-if1", mysarif1);
                    mysarif1.setGraphLink(lastHeader + "-if1");
                    mysar.ifaceSarList.put(lastHeader + "-if2", mysarif2);
                    mysarif2.setGraphLink(lastHeader + "-if2");
                    mysar.pdfList.put(lastHeader + "-if1", mysarif1);
                    mysar.pdfList.put(lastHeader + "-if2", mysarif2);
                    mysar.ifacetreenode.add(myif);
                } else {
                    mysarif2 = (iface2Sar) mysar.ifaceSarList.get(lastHeader + "-if2");
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                mysarif1.add(now, val1, val2, val3, val4, val5, val6, val7);
                return 1;
            }
            if (statType.equals("rxerr/s")) { //DEV		tps	 rd_sec/s  wr_sec/s	 avgrq-sz	 avgqu-sz	  await		svctm	  %util
                if (mysar.underaverage == 1) {
                    return 1;
                }
                iface1Sar mysarif1 = (iface1Sar) mysar.ifaceSarList.get(lastHeader + "-if1");
                iface2Sar mysarif2;
                if (mysarif1 == null) {
                    mysarif1 = new iface1Sar(mysar, lastHeader);
                    mysarif2 = new iface2Sar(mysar, lastHeader);
                    DefaultMutableTreeNode myif = new DefaultMutableTreeNode(lastHeader);
                    if (mysar.myUI != null) {
                        mysarif1.addtotree(myif);
                        mysarif2.addtotree(myif);
                    }
                    mysar.ifaceSarList.put(lastHeader + "-if1", mysarif1);
                    mysarif1.setGraphLink(lastHeader + "-if1");
                    mysar.ifaceSarList.put(lastHeader + "-if2", mysarif2);
                    mysarif2.setGraphLink(lastHeader + "-if2");
                    mysar.pdfList.put(lastHeader + "-if1", mysarif1);
                    mysar.pdfList.put(lastHeader + "-if2", mysarif2);
                    mysar.ifacetreenode.add(myif);
                } else {
                    mysarif2 = (iface2Sar) mysar.ifaceSarList.get(lastHeader + "-if2");
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                mysarif2.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9);
                return 1;
            }
            if (statType.equals("dentunusd")) {
                return 1;
            }
            if (statType.equals("%file-sz")) {
                return 1;
            }
            if (statType.equals("TTY")) {
                return 1;
            }
        } catch (SeriesException e) {
            System.out.println("Linux parser: " + e);
            return -1;
        }
        return 0;
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
    int stattypenum = 0;
    int entreetype;
    int firstwastime;
    // Linux
    cpuSar sarCPU2 = null;
    procSar sarPROC = null;
    cswchSar sarCSWCH = null;
    intrSar sarINTR = null;
    swapSar sarSWAP2 = null;
    ioSar sarIO = null;
    loadSar sarLOAD = null;
    pageSar sarPAGE = null;
    sockSar sarSOCK = null;
    kbmemSar sarKBMEM = null;
    kbswpSar sarKBSWP = null;
    kbmiscSar sarKBMISC = null;
    pgpSar sarPGP = null;
    nfsSar sarNFS = null;
    nfsdSar sarNFSD = null;
    ArrayList<String> intrlist = new ArrayList<String>();
}
