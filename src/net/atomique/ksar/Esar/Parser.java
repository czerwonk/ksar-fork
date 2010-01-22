/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Esar;

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
            this.mysar.myOS = new OSInfo("Esar SunOS", "automatically");
        }
        
        // skip sunos
        matcher.nextToken();
        
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
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IOsSpecificParser#parse(java.lang.String, java.lang.String, java.util.StringTokenizer)
     */
    @Override
    public void parse(String thisLine, String first, StringTokenizer matcher) throws ParsingException {
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
        val12 = null;
        val13 = null;


        /*
         * DISK ID nfs12 added
         */

        if (thisLine.indexOf("DISK ID") > 0) {
            return;
        }
        /*
        00:00:02 CPU  iget/s namei/s dirbk/s
        00:00:02 CPU bread/s lread/s %rcache bwrite/s lwrite/s %wcache phread/s phwrite/s
        00:00:02 CPU scall/s sread/s swrit/s  fork/s vfork/s  exec/s   rchar/s   wchar/s  intr/s  trap/s
        00:00:02    device        %busy  read/s write/s   kbr/s   kbw/s    avqr    avqw  avwait  avserv
        00:00:02 CPU  pgout/s ppgout/s pgfree/s pgscan/s %ufs_ipf
         */
        if (thisLine.indexOf("namei/s") > 0) {
            statType = "namei/s";
            if (!mysar.hasfilenode) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.filetreenode);
                }
                mysar.hasfilenode = true;
            }
            return;
        }
        if (thisLine.indexOf("bread/s") > 0) {
            statType = "bread/s";
            if (!mysar.hasbuffernode) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.buffertreenode);
                }
                mysar.hasbuffernode = true;
            }
            return;
        }
        if (thisLine.indexOf("scall/s") > 0) {
            statType = "scall/s";
            if (!mysar.hasscallnode) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.scalltreenode);
                }
                mysar.hasscallnode = true;
            }
            return;
        }
        if (thisLine.indexOf("avqr") > 0) {
            statType = "avqr";
            if (!mysar.hasdisknode) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.diskstreenode);
                }
                mysar.hasdisknode = true;
            }
            return;
        }
        if (thisLine.indexOf("pgout/s") > 0) {
            statType = "pgout/s";
            if (!mysar.haspaging1node) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.paging1treenode);
                }
                mysar.haspaging1node = true;
            }
            return;
        }
        /*
        00:00:02 CPU   msg/s  sema/s
        00:00:02 CPU  atch/s  pgin/s ppgin/s  pflt/s  vflt/s slock/s
        00:00:02 runq-sz %runocc swpq-sz %swpocc
        00:00:02 freemem freeswap swpunalloc swpreserv swpalloc
        00:00:02 CPU    %usr    %sys    %wio   %idle   %w_io %w_swap  %w_pio
         */
        if (thisLine.indexOf("sema/s") > 0) {
            statType = "sema/s";
            if (!mysar.hasmsgnode) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.msgtreenode);
                }
                mysar.hasmsgnode = true;
            }
            return;
        }
        if (thisLine.indexOf("ppgin/s") > 0) {
            statType = "ppgin/s";
            if (!mysar.haspaging2node) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.paging2treenode);
                }
                mysar.haspaging2node = true;
            }
            return;
        }
        if (thisLine.indexOf("runq-sz") > 0) {
            statType = "runq-sz";
            if (sarRQUEUE == null) {
                sarRQUEUE = new rqueueSar(mysar);
                if (mysar.myUI != null) {
                    sarRQUEUE.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsarRqueueSar", sarRQUEUE);
                sarRQUEUE.setGraphLink("EsarRqueueSar");
            }
            if (sarSQUEUE == null) {
                sarSQUEUE = new squeueSar(mysar);
                if (mysar.myUI != null) {
                    sarSQUEUE.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsarSqueueSar", sarSQUEUE);
                sarSQUEUE.setGraphLink("EsarSqueueSar");
            }
            return;
        }
        if (thisLine.indexOf("freemem") > 0) {
            statType = "freemem";
            if (sarMEM == null) {
                sarMEM = new memSar(mysar);
                if (mysar.myUI != null) {
                    sarMEM.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsarmemSar", sarMEM);
                sarMEM.setGraphLink("EsarmemSar");
            }
            return;
        }
        if (thisLine.indexOf("%w_io") > 0) {
            statType = "%w_io";
            if (!mysar.hascpunode) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.cpustreenode);
                }
                mysar.hascpunode = true;
            }
            return;
        }
        /*
        00:00:02 CPU swpin/s bswin/s swpot/s bswot/s pswch/s
        00:00:02 CPU rawch/s canch/s outch/s rcvin/s xmtin/s mdmin/s
        00:00:02     la1     la5    la15   nproc
        00:00:02     interface  ipacket/s  opacket/s    ibits/s    obits/s   ierror/s   oerror/s      %util
        00:00:02     interface bcstrcv/s bcstxmt/s mcstrcv/s mcstxmt/s norcvbf/s noxmtbf/s    coll/s
         */
        if (thisLine.indexOf("swpin/s") > 0) {
            statType = "swpin/s";
            if (!mysar.hasswapingnode) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.swapingtreenode);
                }
                mysar.hasswapingnode = true;
            }
            return;
        }
        if (thisLine.indexOf("rawch/s") > 0) {
            statType = "rawch/s";
            if (!mysar.hasttynode) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.ttytreenode);
                }
                mysar.hasttynode = true;
            }
            return;
        }
        if (thisLine.indexOf("nproc") > 0) {
            statType = "nproc";
            if (sarLOAD == null) {
                sarLOAD = new loadSar(mysar);
                if (mysar.myUI != null) {
                    sarLOAD.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsarloadSar", sarLOAD);
                sarLOAD.setGraphLink("EsarloadSar");
            }
            return;
        }
        if (thisLine.indexOf("ipacket/s") > 0) {
            statType = "ipacket/s";
            if (!mysar.hasifnode) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.ifacetreenode);
                }
                mysar.hasifnode = true;
            }
            return;
        }
        if (thisLine.indexOf("bcstrcv/s") > 0) {
            statType = "bcstrcv/s";
            if (!mysar.hasifnode) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.ifacetreenode);
                }
                mysar.hasifnode = true;
            }
            return;
        }
        /*
        00:00:02 pset     la1     la5    la15   ncpus
        00:00:02 RAWIP   inDgms/s   inErrs/s  outDgms/s  outErrs/s  CksErrs/s
        00:00:02 UDP     inDgms/s   inErrs/s  outDgms/s  outErrs/s
        00:00:02 TCP    actvOpens/s atmptFails/s    currEstab  estabRsts/s hlfOpenDrp/s listenDrop/s listDropQ0/s passvOpens/s
         */
        if (thisLine.indexOf("pset") > 0) {
            statType = "pset";
            if (!mysar.haspsetnode) {
                if (mysar.myUI != null) {
                    mysar.add2tree(mysar.graphtree, mysar.psettreenode);
                }
                mysar.haspsetnode = true;
            }
            return;
        }
        if (thisLine.indexOf("RAWIP") > 0) {
            statType = "RAWIP";
            if (sarRAWIP == null) {
                sarRAWIP = new rawipSar(mysar);
                if (mysar.myUI != null) {
                    sarRAWIP.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsarrawipSar", sarRAWIP);
                sarRAWIP.setGraphLink("EsarrawipSar");
            }
            return;
        }
        if (thisLine.indexOf("UDP") > 0) {
            statType = "UDP";
            if (sarUDP == null) {
                sarUDP = new udpSar(mysar);
                if (mysar.myUI != null) {
                    sarUDP.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsarudpSar", sarUDP);
                sarUDP.setGraphLink("EsarudpSar");
            }
            return;
        }
        if (thisLine.indexOf("TCP") > 0) {
            statType = "TCP";
            if (sarTCP == null) {
                sarTCP = new tcpSar(mysar);
                if (mysar.myUI != null) {
                    sarTCP.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsartcpSar", sarTCP);
                sarTCP.setGraphLink("EsartcpSar");
            }
            return;
        }
        /*
        00:00:02 RPCD(tcp) badcalls   badlen    calls   dupchk  dupreqs  nullrcv  xdrcall
        00:00:02 RPC(tcp)  badcalls badverfs  badxids    calls cantconn  intrpts newcreds    nomem timeouts   timers
        00:00:02 RPCD(udp) badcalls   badlen    calls   dupchk  dupreqs  nullrcv  xdrcall
        00:00:02 RPC(udp)  badcalls badverfs  badxids    calls cantsend newcreds    nomem  retrans timeouts   timers
         */
        if (thisLine.indexOf("RPCD(tcp)") > 0) {
            statType = "RPCD(tcp)";
            if (sarRPCDTCP == null) {
                sarRPCDTCP = new rpcdtcpSar(mysar);
                if (mysar.myUI != null) {
                    sarRPCDTCP.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsarrpcdtcpSar", sarRPCDTCP);
                sarRPCDTCP.setGraphLink("EsarrpcdtcpSar");
            }
            return;
        }
        if (thisLine.indexOf("RPC(tcp)") > 0) {
            statType = "RPC(tcp)";
            if (sarRPCTCP == null) {
                sarRPCTCP = new rpctcpSar(mysar);
                if (mysar.myUI != null) {
                    sarRPCTCP.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsarrpctcpSar", sarRPCTCP);
                sarRPCTCP.setGraphLink("EsarrpctcpSar");
            }
            return;
        }
        if (thisLine.indexOf("RPCD(udp)") > 0) {
            statType = "RPCD(udp)";
            if (sarRPCDUDP == null) {
                sarRPCDUDP = new rpcdudpSar(mysar);
                if (mysar.myUI != null) {
                    sarRPCDUDP.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsarrpcdudpSar", sarRPCDUDP);
                sarRPCDUDP.setGraphLink("EsarrpcdudpSar");
            }
            return;
        }
        if (thisLine.indexOf("RPC(udp)") > 0) {
            statType = "RPC(udp)";
            if (sarRPCUDP == null) {
                sarRPCUDP = new rpcudpSar(mysar);
                if (mysar.myUI != null) {
                    sarRPCUDP.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsarrpcudpSar", sarRPCUDP);
                sarRPCUDP.setGraphLink("EsarrpcudpSar");
            }
            return;
        }
        /*
        00:00:02 NFS v2   create     link   lookup    mkdir     read  readdir  readlnk   remove   rename    rmdir   symlnk    write
        00:00:02 NFS v2  getattr     null     root  setattr   statfs  wrcache
        00:00:02 NFSD v2   create     link   lookup    mkdir     read  readdir  readlnk   remove   rename    rmdir   symlnk    write
        00:00:02 NFSD v2  getattr     null     root  setattr   statfs  wrcache
        00:00:02 NFS v3   access   commit   create     link   lookup    mkdir     read  readlnk   remove   rename    rmdir  symlink    write
        00:00:02 NFS v3   fsstat  getattr   fsinfo     null pathconf  setattr    mknod  readdir  readirp
        00:00:02 NFSD v3   access   commit   create     link   lookup    mkdir     read  readlnk   remove   rename    rmdir  symlink    write
        00:00:02 NFSD v3   fsstat  getattr   fsinfo     null pathconf  setattr    mknod  readdir  readirp
        00:00:02 NFS v4   access    close   create     link     open     read  readdir  readlnk   remove   rename    renew   verify    write
        00:00:02 NFS v4   commit   compnd   dpurge  dreturn  getattr   lookup  lookupp     null  nverify  opencnf  opendwg  openatr  reservd  secinfo  setattr
        00:00:02 NFS v4     lock    lockt    locku    getfh    putfh  ptpubfh   ptrtfh   rstrfh   savefh    setid setidcnf
        00:00:02 NFSD v4   access    close   create     link     open     read  readdir  readlnk   remove   rename    renew   verify    write
        00:00:02 NFSD v4   commit   compnd   dpurge  dreturn  getattr   lookup  lookupp     null  nverify  opencnf  opendwg  openatr  reservd  secinfo  setattr
        00:00:02 NFSD v4     lock    lockt    locku    getfh    putfh  ptpubfh   ptrtfh   rstrfh   savefh    setid setidcnf rlckownr  illegal        
         */
        if (thisLine.indexOf("NFS v2   create") > 0) {
            statType = "NFSv21";
            if (sarNFSv2 == null) {
                sarNFSv2 = new nfsv2Sar(mysar);
                if (mysar.myUI != null) {
                    if (!mysar.hasnfsnode) {                        
                        mysar.add2tree(mysar.graphtree, mysar.nfstreenode);                        
                        mysar.hasnfsnode = true;
                    }
                    sarNFSv2.addtotree(mysar.nfstreenode);
                }
                mysar.pdfList.put("Esarnfsv2Sar", sarNFSv2);
                sarNFSv2.setGraphLink("Esarnfsv2Sar");
            }
            return;
        }
        if (thisLine.indexOf("NFS v2  getattr") > 0) {
            statType = "NFSv22";
            if (sarNFSv2 == null) {
                sarNFSv2 = new nfsv2Sar(mysar);
                if (mysar.myUI != null) {
                    if (!mysar.hasnfsnode) {                        
                        mysar.add2tree(mysar.graphtree, mysar.nfstreenode);                        
                        mysar.hasnfsnode = true;
                    }
                    sarNFSv2.addtotree(mysar.nfstreenode);
                }
                mysar.pdfList.put("Esarnfsv2Sar", sarNFSv2);
                sarNFSv2.setGraphLink("Esarnfsv2Sar");
            }
            return;
        }
        if (thisLine.indexOf("NFSD v2   create") > 0) {
            statType = "NFSDv21";
            if (sarNFSDv2 == null) {
                sarNFSDv2 = new nfsdv2Sar(mysar);
                if (mysar.myUI != null) {
                    if (!mysar.hasnfsnode) {                        
                        mysar.add2tree(mysar.graphtree, mysar.nfstreenode);                        
                        mysar.hasnfsnode = true;
                    }
                    sarNFSDv2.addtotree(mysar.nfstreenode);
                }
                mysar.pdfList.put("Esarnfsdv2Sar", sarNFSDv2);
                sarNFSDv2.setGraphLink("Esarnfsdv2Sar");
            }
            return;
        }
        if (thisLine.indexOf("NFSD v2  getattr") > 0) {
            statType = "NFSDv22";
            if (sarNFSDv2 == null) {
                sarNFSDv2 = new nfsdv2Sar(mysar);
                if (mysar.myUI != null) {
                    if (!mysar.hasnfsnode) {                        
                        mysar.add2tree(mysar.graphtree, mysar.nfstreenode);                        
                        mysar.hasnfsnode = true;
                    }
                    sarNFSDv2.addtotree(mysar.nfstreenode);
                }
                mysar.pdfList.put("Esarnfsdv2Sar", sarNFSDv2);
                sarNFSDv2.setGraphLink("Esarnfsdv2Sar");
            }
            return;
        }
        if (thisLine.indexOf("NFS v3   access") > 0) {
            statType = "NFSv31";
            if (sarNFSv3 == null) {
                sarNFSv3 = new nfsv3Sar(mysar);
                if (mysar.myUI != null) {
                    if (!mysar.hasnfsnode) {                        
                        mysar.add2tree(mysar.graphtree, mysar.nfstreenode);                        
                        mysar.hasnfsnode = true;
                    }
                    sarNFSv3.addtotree(mysar.nfstreenode);
                }
                mysar.pdfList.put("Esarnfsv3Sar", sarNFSv3);
                sarNFSv3.setGraphLink("Esarnfsv3Sar");
            }
            return;
        }
        if (thisLine.indexOf("NFS v3   fsstat") > 0) {
            statType = "NFSv32";
            if (sarNFSv3 == null) {
                sarNFSv3 = new nfsv3Sar(mysar);
                if (mysar.myUI != null) {
                    if (!mysar.hasnfsnode) {                        
                        mysar.add2tree(mysar.graphtree, mysar.nfstreenode);                        
                        mysar.hasnfsnode = true;
                    }
                    sarNFSv3.addtotree(mysar.nfstreenode);
                }
                mysar.pdfList.put("Esarnfsv3Sar", sarNFSv3);
                sarNFSv3.setGraphLink("Esarnfsv3Sar");
            }
            return;
        }
        if (thisLine.indexOf("NFSD v3   access") > 0) {
            statType = "NFSDv31";
            if (sarNFSDv3 == null) {
                sarNFSDv3 = new nfsdv3Sar(mysar);
                if (mysar.myUI != null) {
                    if (!mysar.hasnfsnode) {                        
                        mysar.add2tree(mysar.graphtree, mysar.nfstreenode);                        
                        mysar.hasnfsnode = true;
                    }
                    sarNFSDv3.addtotree(mysar.nfstreenode);
                }
                mysar.pdfList.put("Esarnfsdv3Sar", sarNFSDv3);
                sarNFSDv3.setGraphLink("Esarnfsdv3Sar");
            }
            return;
        }
        if (thisLine.indexOf("NFSD v3   fsstat") > 0) {
            statType = "NFSDv32";
            if (sarNFSDv3 == null) {
                sarNFSDv3 = new nfsdv3Sar(mysar);
                if (mysar.myUI != null) {
                    if (!mysar.hasnfsnode) {                        
                        mysar.add2tree(mysar.graphtree, mysar.nfstreenode);                        
                        mysar.hasnfsnode = true;
                    }
                    sarNFSDv3.addtotree(mysar.nfstreenode);
                }
                mysar.pdfList.put("Esarnfsdv3Sar", sarNFSDv3);
                sarNFSDv3.setGraphLink("Esarnfsdv3Sar");
            }
            return;
        }
        if (thisLine.indexOf("NFS v4   access") > 0) {
            statType = "NFSv41";
            return;
        }
        if (thisLine.indexOf("NFS v4   commit") > 0) {
            statType = "NFSv42";
            return;
        }
        if (thisLine.indexOf("NFS v4     lock") > 0) {
            statType = "NFSv43";
            return;
        }
        if (thisLine.indexOf("NFSD v4   access") > 0) {
            statType = "NFSDv41";
            return;
        }
        if (thisLine.indexOf("NFSD v4   commit") > 0) {
            statType = "NFSDv42";
            return;
        }
        if (thisLine.indexOf("NFSD v4     lock") > 0) {
            statType = "NFSDv43";
            return;
        }
        /*
        00:00:02     Total    Kernel    Locked     Avail
         */
        if (thisLine.indexOf("Locked") > 0) {
            statType = "Locked";
            if (sarMEMORY == null) {
                sarMEMORY = new memorySar(mysar);
                if (mysar.myUI != null) {
                    sarMEMORY.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsarmemorySar", sarMEMORY);
                sarMEMORY.setGraphLink("EsarmemorySar");
            }
            return;
        }
        /*
        00:00:00 IP   inRcvs/s  inDlvrs/s  noPorts/s outRqsts/s
        */
        if (thisLine.indexOf("IP inRcvs/s") > 0) {
            statType = "IP";
            if (sarIP == null) {
                sarIP = new ipSar(mysar);
                if (mysar.myUI != null) {
                    sarIP.addtotree(mysar.graphtree);
                }
                mysar.pdfList.put("EsaripSar", sarIP);
                sarIP.setGraphLink("EsaripSar");
            }
            return;
        } 
        /*
         *  PARSING
         */

        String[] sarTime = first.split(":");
        if (sarTime.length != 3) {
            return;
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

        try {
            if (statType.equals("%w_io")) {
                String cpuid = matcher.nextToken();
                cpuSar mysarcpu = (cpuSar) mysar.cpuSarList.get(cpuid + "-cpu");
                if (mysarcpu == null) {
                    mysarcpu = new cpuSar(mysar, cpuid);
                    //DefaultMutableTreeNode mycpu = new DefaultMutableTreeNode("cpu-" + cpuid);
                    mysar.cpuSarList.put(cpuid + "-cpu", mysarcpu);
                    mysar.pdfList.put(cpuid + "-cpu", mysarcpu);
                    mysarcpu.setGraphLink("cpu-" + cpuid);
                    if (mysar.myUI != null) {
                        mysarcpu.addtotree(mysar.cpustreenode);
                    }
                }

                // CPU    %usr    %sys    %wio   %idle   %w_io %w_swap  %w_pio
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                //
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                mysarcpu.add(now, val1, val2, val3, val4, val5, val6, val7);
                return;
            }
            if (statType.equals("namei/s")) {
                String cpuid = matcher.nextToken();
                fileSar mysarfile = (fileSar) mysar.fileSarList.get(cpuid + "-file");
                if (mysarfile == null) {
                    mysarfile = new fileSar(mysar, cpuid);
                    mysar.fileSarList.put(cpuid + "-file", mysarfile);
                    mysar.pdfList.put(cpuid + "-file", mysarfile);
                    mysarfile.setGraphLink("file-" + cpuid);
                    if (mysar.myUI != null) {
                        mysarfile.addtotree(mysar.filetreenode);
                    }
                }

                // CPU  iget/s namei/s dirbk/s
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                mysarfile.add(now, val1, val2, val3);
                return;
            }

            if (statType.equals("scall/s")) {
                String cpuid = matcher.nextToken();
                syscallSar mysarscall = (syscallSar) mysar.scallSarList.get(cpuid + "-syscall");
                if (mysarscall == null) {
                    mysarscall = new syscallSar(mysar, cpuid);
                    mysar.scallSarList.put(cpuid + "-syscall", mysarscall);
                    mysar.pdfList.put(cpuid + "-syscall", mysarscall);
                    mysarscall.setGraphLink("syscall-" + cpuid);
                    if (mysar.myUI != null) {
                        mysarscall.addtotree(mysar.scalltreenode);
                    }
                }

                // CPU  iget/s namei/s dirbk/s
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                val10 = new Float(matcher.nextToken());
                mysarscall.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9, val10);
                return;
            }
            if (statType.equals("bread/s")) {
                String cpuid = matcher.nextToken();
                bufferSar mysarbuffer = (bufferSar) mysar.bufferSarList.get(cpuid + "-buffer");
                if (mysarbuffer == null) {
                    mysarbuffer = new bufferSar(mysar, cpuid);
                    mysar.bufferSarList.put(cpuid + "-buffer", mysarbuffer);
                    mysar.pdfList.put(cpuid + "-buffer", mysarbuffer);
                    mysarbuffer.setGraphLink("buffer-" + cpuid);
                    if (mysar.myUI != null) {
                        mysarbuffer.addtotree(mysar.buffertreenode);
                    }
                }

                // CPU bread/s lread/s %rcache bwrite/s lwrite/s %wcache phread/s phwrite/s
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                mysarbuffer.add(now, val1, val2, val3, val4, val5, val6, val7, val8);
                return;
            }

            if (statType.equals("avqr")) {
                diskwaitSar mydiskwait = null;
                String disktag = matcher.nextToken();
                diskxferSar mydiskxfer = (diskxferSar) mysar.disksSarList.get(disktag + "Esardiskxfer");
                if (mydiskxfer == null) {
                    diskName tmp = new diskName(disktag);
                    mysar.AlternateDiskName.put(disktag, tmp);
                    tmp.setTitle((String) mysar.Adiskname.get(disktag));
                    mydiskxfer = new diskxferSar(mysar, disktag, tmp);
                    mysar.disksSarList.put(disktag + "Esardiskxfer", mydiskxfer);
                    mydiskwait = new diskwaitSar(mysar, disktag, tmp);
                    mysar.disksSarList.put(disktag + "Esardiskwait", mydiskwait);
                    DefaultMutableTreeNode mydisk = new DefaultMutableTreeNode(tmp.showTitle());
                    mysar.pdfList.put(disktag + "Esardiskxfer", mydiskxfer);
                    mydiskxfer.addtotree(mydisk);
                    mydiskwait.addtotree(mydisk);
                    mysar.pdfList.put(disktag + "Esardiskwait", mydiskwait);
                    mydiskxfer.setGraphLink(disktag + "Esardiskxfer");
                    mydiskwait.setGraphLink(disktag + "Esardiskwait");
                    mysar.add2tree(mysar.diskstreenode, mydisk);
                } else {
                    mydiskwait = (diskwaitSar) mysar.disksSarList.get(disktag + "Esardiskwait");
                }
                //    device        %busy  read/s write/s   kbr/s   kbw/s    avqr    avqw  avwait  avserv
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                mydiskxfer.add(now, val2, val3, val4, val5, val9);
                mydiskwait.add(now, val1, val6, val7, val8);
                return;
            }
            if (statType.equals("pgout/s")) {
                String cpuid = matcher.nextToken();
                paging1Sar mysarpaging1 = (paging1Sar) mysar.paging1SarList.get(cpuid + "-paging1");
                if (mysarpaging1 == null) {
                    mysarpaging1 = new paging1Sar(mysar, cpuid);
                    mysar.paging1SarList.put(cpuid + "-paging1", mysarpaging1);
                    mysar.pdfList.put(cpuid + "-paging1", mysarpaging1);
                    mysarpaging1.setGraphLink("paging1-" + cpuid);
                    if (mysar.myUI != null) {
                        mysarpaging1.addtotree(mysar.paging1treenode);
                    }
                }

                // CPU  pgout/s ppgout/s pgfree/s pgscan/s %ufs_ipf
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                mysarpaging1.add(now, val1, val2, val3, val4, val5);
                return;
            }
            if (statType.equals("sema/s")) {
                String cpuid = matcher.nextToken();
                msgSar mysarmsg = (msgSar) mysar.msgSarList.get(cpuid + "-msg");
                if (mysarmsg == null) {
                    mysarmsg = new msgSar(mysar, cpuid);
                    mysar.msgSarList.put(cpuid + "-msg", mysarmsg);
                    mysar.pdfList.put(cpuid + "-msg", mysarmsg);
                    mysarmsg.setGraphLink("msg-" + cpuid);
                    if (mysar.myUI != null) {
                        mysarmsg.addtotree(mysar.msgtreenode);
                    }
                }

                // CPU   msg/s  sema/s
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                mysarmsg.add(now, val1, val2);
                return;
            }
            if (statType.equals("ppgin/s")) {
                String cpuid = matcher.nextToken();
                paging2Sar mysarpaging2 = (paging2Sar) mysar.paging2SarList.get(cpuid + "-paging2");
                if (mysarpaging2 == null) {
                    mysarpaging2 = new paging2Sar(mysar, cpuid);
                    mysar.paging2SarList.put(cpuid + "-paging2", mysarpaging2);
                    mysar.pdfList.put(cpuid + "-paging2", mysarpaging2);
                    mysarpaging2.setGraphLink("paging2-" + cpuid);
                    if (mysar.myUI != null) {
                        mysarpaging2.addtotree(mysar.paging2treenode);
                    }
                }

                // CPU  atch/s  pgin/s ppgin/s  pflt/s  vflt/s slock/s
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                mysarpaging2.add(now, val1, val2, val3, val4, val5, val6);
                return;
            }

            if (statType.equals("runq-sz")) {
                if (matcher.hasMoreElements()) {
                    val1 = new Float(matcher.nextToken());
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
            if (statType.equals("freemem")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                sarMEM.add(now, val1, val2);
                return;
            }
            if (statType.equals("swpin/s")) {
                String cpuid = matcher.nextToken();
                swapingSar mysarswaping = (swapingSar) mysar.swapingSarList.get(cpuid + "-swaping");
                if (mysarswaping == null) {
                    mysarswaping = new swapingSar(mysar, cpuid);
                    mysar.swapingSarList.put(cpuid + "-swaping", mysarswaping);
                    mysar.pdfList.put(cpuid + "-swaping", mysarswaping);
                    mysarswaping.setGraphLink("swaping-" + cpuid);
                    if (mysar.myUI != null) {
                        mysarswaping.addtotree(mysar.swapingtreenode);
                    }
                }

                // CPU swpin/s bswin/s swpot/s bswot/s pswch/s
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                mysarswaping.add(now, val1, val2, val3, val4, val5);
                return;
            }
            if (statType.equals("rawch/s")) {
                String cpuid = matcher.nextToken();
                ttySar mysartty = (ttySar) mysar.ttySarList.get(cpuid + "-tty");
                if (mysartty == null) {
                    mysartty = new ttySar(mysar, cpuid);
                    mysar.ttySarList.put(cpuid + "-tty", mysartty);
                    mysar.pdfList.put(cpuid + "-tty", mysartty);
                    mysartty.setGraphLink("tty-" + cpuid);
                    if (mysar.myUI != null) {
                        mysartty.addtotree(mysar.ttytreenode);
                    }
                }

                // CPU rawch/s canch/s outch/s rcvin/s xmtin/s mdmin/s
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                mysartty.add(now, val1, val2, val3, val4, val5, val6);
                return;
            }
            if (statType.equals("nproc")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                sarLOAD.add(now, val1, val2, val3, val4);
                return;
            }
            if (statType.equals("ipacket/s")) {
                String ifname = matcher.nextToken();
                iface1Sar mysarif1 = (iface1Sar) mysar.ifaceSarList.get(ifname + "-if1");
                iface2Sar mysarif2;
                if (mysarif1 == null) {
                    mysarif1 = new iface1Sar(mysar, ifname);
                    mysarif2 = new iface2Sar(mysar, ifname);
                    DefaultMutableTreeNode myif = new DefaultMutableTreeNode(ifname);
                    if (mysar.myUI != null) {
                        mysarif1.addtotree(myif);
                        mysarif2.addtotree(myif);
                    }
                    mysar.ifaceSarList.put(ifname + "-if1", mysarif1);
                    mysarif1.setGraphLink(ifname + "-if1");
                    mysar.ifaceSarList.put(ifname + "-if2", mysarif2);
                    mysarif2.setGraphLink(ifname + "-if2");
                    mysar.pdfList.put(ifname + "-if1", mysarif1);
                    mysar.pdfList.put(ifname + "-if2", mysarif2);
                    mysar.ifacetreenode.add(myif);
                } else {
                    mysarif2 = (iface2Sar) mysar.ifaceSarList.get(ifname + "-if2");
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                String tmpval7 = matcher.nextToken();
                if ("N/A".equals(tmpval7)) {
                    val7 = new Float(0);
                } else {
                    val7 = new Float(tmpval7);
                }
                mysarif1.add(now, val1, val2, val3, val4, val5, val6, val7);
                return;
            }
            if (statType.equals("bcstrcv/s")) {
                String ifname = matcher.nextToken();
                iface1Sar mysarif1 = (iface1Sar) mysar.ifaceSarList.get(ifname + "-if1");
                iface2Sar mysarif2;
                if (mysarif1 == null) {
                    mysarif1 = new iface1Sar(mysar, ifname);
                    mysarif2 = new iface2Sar(mysar, ifname);
                    DefaultMutableTreeNode myif = new DefaultMutableTreeNode(ifname);
                    if (mysar.myUI != null) {
                        mysarif1.addtotree(myif);
                        mysarif2.addtotree(myif);
                    }
                    mysar.ifaceSarList.put(ifname + "-if1", mysarif1);
                    mysarif1.setGraphLink(ifname + "-if1");
                    mysar.ifaceSarList.put(ifname + "-if2", mysarif2);
                    mysarif2.setGraphLink(ifname + "-if2");
                    mysar.pdfList.put(ifname + "-if1", mysarif1);
                    mysar.pdfList.put(ifname + "-if2", mysarif2);
                    mysar.ifacetreenode.add(myif);
                } else {
                    mysarif2 = (iface2Sar) mysar.ifaceSarList.get(ifname + "-if2");
                }
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                mysarif2.add(now, val1, val2, val3, val4, val5, val6, val7);
                return;
            }
            if (statType.equals("pset")) {
                String psetid = matcher.nextToken();
                psetSar mysarpset = (psetSar) mysar.psetSarList.get(psetid + "-pset");
                if (mysarpset == null) {
                    mysarpset = new psetSar(mysar, psetid);
                    mysar.psetSarList.put(psetid + "-pset", mysarpset);
                    mysar.pdfList.put(psetid + "-pset", mysarpset);
                    mysarpset.setGraphLink("pset-" + psetid);
                    if (mysar.myUI != null) {
                        mysarpset.addtotree(mysar.psettreenode);
                    }
                }

                // CPU rawch/s canch/s outch/s rcvin/s xmtin/s mdmin/s
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                mysarpset.add(now, val1, val2, val3, val4);
                return;
            }
            if (statType.equals("RAWIP")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                sarRAWIP.add(now, val1, val2, val3, val4, val5);
                return;
            }
            if (statType.equals("UDP")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                sarUDP.add(now, val1, val2, val3, val4);
                return;
            }
            if (statType.equals("IP")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                sarIP.add(now, val1, val2, val3, val4);
                return;
            }
            if (statType.equals("TCP")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                sarTCP.add(now, val1, val2, val3, val4, val5, val6, val7, val8);
                return;
            }
            if (statType.equals("RPC(udp)")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                val10 = new Float(matcher.nextToken());
                sarRPCUDP.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9, val10);
                return;
            }
            if (statType.equals("RPC(tcp)")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                val10 = new Float(matcher.nextToken());
                sarRPCTCP.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9, val10);
                return;
            }
            if (statType.equals("RPCD(tcp)")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                sarRPCDTCP.add(now, val1, val2, val3, val4, val5, val6, val7);
                return;
            }
            if (statType.equals("RPCD(udp)")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                sarRPCDUDP.add(now, val1, val2, val3, val4, val5, val6, val7);
                return;
            }
            if (statType.equals("Locked")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                sarMEMORY.add(now, val1, val2, val3, val4);
                return;
            }
            if (statType.equals("NFSDv31")) {
                val1 = new Float(matcher.nextToken());
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
                val12 = new Float(matcher.nextToken());
                val13 = new Float(matcher.nextToken());
                sarNFSDv3.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9, val10, val11, val12, val13);
                return;
            }
            if (statType.equals("NFSDv32")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                sarNFSDv3.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9);
                return;
            }
            if (statType.equals("NFSv31")) {
                val1 = new Float(matcher.nextToken());
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
                val12 = new Float(matcher.nextToken());
                val13 = new Float(matcher.nextToken());
                sarNFSv3.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9, val10, val11, val12, val13);
                return;
            }
            if (statType.equals("NFSv32")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                sarNFSv3.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9);
                return;
            }
            if (statType.equals("NFSDv21")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                val10 = new Float(matcher.nextToken());                
                sarNFSDv2.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9, val10);
                return;
            }
            if (statType.equals("NFSDv22")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());                
                sarNFSDv2.add(now, val1, val2, val3, val4, val5, val6);
                return;
            }
            if (statType.equals("NFSv21")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());
                val7 = new Float(matcher.nextToken());
                val8 = new Float(matcher.nextToken());
                val9 = new Float(matcher.nextToken());
                val10 = new Float(matcher.nextToken());                
                sarNFSv2.add(now, val1, val2, val3, val4, val5, val6, val7, val8, val9, val10);
                return;
            }
            if (statType.equals("NFSv22")) {
                val1 = new Float(matcher.nextToken());
                val2 = new Float(matcher.nextToken());
                val3 = new Float(matcher.nextToken());
                val4 = new Float(matcher.nextToken());
                val5 = new Float(matcher.nextToken());
                val6 = new Float(matcher.nextToken());                
                sarNFSv2.add(now, val1, val2, val3, val4, val5, val6);
                return;
            }
            /*
             */
            System.out.println("Esar unknwon line ("+statType+"): " + thisLine);
        /*
         */
        } catch (SeriesException e) {
            throw new ParsingException("Esar parser: " + e);
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
    Float val12;
    Float val13;
    int heure = 0;
    int minute = 0;
    int seconde = 0;
    Second now = new Second(0, 0, 0, 1, 1, 1970);
    String statType = "none";
    int stattypenum = 0;
    int entreetype;
    int firstwastime;
    //
    private squeueSar sarSQUEUE = null;
    private rqueueSar sarRQUEUE = null;
    private memSar sarMEM = null;
    private loadSar sarLOAD = null;
    private rawipSar sarRAWIP = null;
    private udpSar sarUDP = null;
    private tcpSar sarTCP = null;
    private rpctcpSar sarRPCTCP = null;
    private rpcudpSar sarRPCUDP = null;
    private rpcdtcpSar sarRPCDTCP = null;
    private rpcdudpSar sarRPCDUDP = null;
    private memorySar sarMEMORY = null;
    private nfsdv2Sar sarNFSDv2 = null;
    private nfsv2Sar sarNFSv2 = null;
    private nfsdv3Sar sarNFSDv3 = null;
    private nfsv3Sar sarNFSv3 = null;
    private ipSar sarIP = null;
}
