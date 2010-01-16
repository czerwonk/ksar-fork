/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;

import java.awt.Color;
import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.kSar;
import net.atomique.ksar.kSarConfig;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author alex
 */
public class pgpSar extends AllGraph {

    public pgpSar(kSar hissar) {
        super(hissar);
        Title = new String("Paging Activity");
        t_pgpgin = new TimeSeries("pgpgin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("pgpgin/s", t_pgpgin);
        t_pgpgout = new TimeSeries("pgpgout/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("pgpgout/s", t_pgpgout);
        t_fault = new TimeSeries("fault/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("fault/s", t_fault);
        t_majflt = new TimeSeries("majflt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("majflt/s", t_majflt);
        t_activepg = new TimeSeries("activepg", org.jfree.data.time.Second.class);
        mysar.dispo.put("activepg", t_activepg);
        t_inadtypg = new TimeSeries("inadtypg", org.jfree.data.time.Second.class);
        mysar.dispo.put("inadtypg", t_inadtypg);
        t_inaclnpg = new TimeSeries("inaclnpg", org.jfree.data.time.Second.class);
        mysar.dispo.put("inaclnpg", t_inaclnpg);
        t_inatarpg = new TimeSeries("inatargp", org.jfree.data.time.Second.class);
        mysar.dispo.put("inatarpg", t_inatarpg);
        //  pgfree/s pgscank/s pgscand/s pgsteal/s    %vmeff
        t_pgfree = new TimeSeries("pgfree/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("pgfree/s", t_pgfree);
        
        t_pgscank = new TimeSeries("pgscank/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("pgscank/s", t_pgscank);
        
        t_pgscand = new TimeSeries("pgscand/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("pgscand/s", t_pgscand);
        
        t_pgsteal = new TimeSeries("pgsteal/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("pgsteal/s", t_pgsteal);
        
        t_vmeff = new TimeSeries("%vmeff", org.jfree.data.time.Second.class);
        mysar.dispo.put("%vmeff", t_vmeff);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4) {
        this.t_pgpgin.add(now, val1, do_notify());
        this.t_pgpgout.add(now, val2, do_notify());
        this.t_fault.add(now, val3, do_notify());
        this.t_majflt.add(now, val4, do_notify());
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5, Float val6) {
        this.t_pgpgin.add(now, val1, do_notify());
        this.t_pgpgout.add(now, val2, do_notify());
        this.t_activepg.add(now, val3, do_notify());
        this.t_inadtypg.add(now, val4, do_notify());
        this.t_inaclnpg.add(now, val5, do_notify());
        this.t_inatarpg.add(now, val6, do_notify());
        number_of_sample++;
    }
// pgpgin/s pgpgout/s   fault/s  majflt/s  pgfree/s pgscank/s pgscand/s pgsteal/s    %vmeff
    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5, Float val6, Float val7, Float val8, Float val9) {
        this.t_pgpgin.add(now, val1, do_notify());
        this.t_pgpgout.add(now, val2, do_notify());
        this.t_fault.add(now, val3, do_notify());
        this.t_majflt.add(now, val4, do_notify());
        this.t_pgfree.add(now, val5, do_notify());
        this.t_pgscank.add(now, val6, do_notify());
        this.t_pgscand.add(now, val7, do_notify());
        this.t_pgsteal.add(now, val8, do_notify());
        this.t_vmeff.add(now, val9, do_notify());
        number_of_sample++;
    }
    
    public XYDataset createpgpg() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_pgpgin);
        graphcollection.addSeries(this.t_pgpgout);
        return graphcollection;
    }

    public XYDataset createfault() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_fault);
        graphcollection.addSeries(this.t_majflt);
        return graphcollection;
    }

    public XYDataset createactive() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_activepg);
        return graphcollection;
    }

    public XYDataset createinact() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_inadtypg);
        graphcollection.addSeries(this.t_inaclnpg);
        graphcollection.addSeries(this.t_inatarpg);
        return graphcollection;
    }

    public void setpgpOpt(String s) {
        this.pgpOpt = s;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXPGP", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        CombinedDomainXYPlot plot = null;
        // tps
        XYDataset pgpg = this.createpgpg();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(pgpg, null, new NumberAxis("pgpgin/pgpgout /s"), minichart1);
        // rws
        XYPlot subplot2 = null;
        XYPlot subplot3 = null;
        if (!pgpOpt.equals("activepg")) {
            XYDataset faultset = this.createfault();
            XYItemRenderer minichart2 = new StandardXYItemRenderer();
            minichart2.setSeriesPaint(0, kSarConfig.color3);
            minichart2.setSeriesPaint(1, kSarConfig.color4);
            minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            subplot2 = new XYPlot(faultset, null, new NumberAxis("fault/majflt /s"), minichart2);
        }
        if (pgpOpt.equals("activepg")) {
            XYDataset actset = this.createactive();
            XYItemRenderer minichart2 = new StandardXYItemRenderer();
            minichart2.setSeriesPaint(0, kSarConfig.color3);
            minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            subplot2 = new XYPlot(actset, null, new NumberAxis("activepg"), minichart2);
            XYDataset inactset = this.createinact();
            XYItemRenderer minichart3 = new StandardXYItemRenderer();
            minichart3.setSeriesPaint(0, kSarConfig.color4);
            minichart3.setSeriesPaint(1, kSarConfig.color5);
            minichart3.setSeriesPaint(2, kSarConfig.color6);
            minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            subplot3 = new XYPlot(inactset, null, new NumberAxis("inadtypg/inaclnpg/inatarpg"), minichart3);
        }

        // the graph
        plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        if (pgpOpt.equals("activepg")) {
            plot.add(subplot3, 1);
        }
        plot.setOrientation(PlotOrientation.VERTICAL);

        // the graph
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
        }
        return mychart;
    }
    private TimeSeries t_pgpgin;
    private TimeSeries t_pgpgout;
    private TimeSeries t_fault;
    private TimeSeries t_majflt;
    private TimeSeries t_activepg;
    private TimeSeries t_inadtypg;
    private TimeSeries t_inaclnpg;
    private TimeSeries t_inatarpg;
    private TimeSeries t_pgfree;
    private TimeSeries t_pgscank;
    private TimeSeries t_pgscand;
    private TimeSeries t_pgsteal;
    private TimeSeries t_vmeff;
    
    private String pgpOpt = new String("");
}
