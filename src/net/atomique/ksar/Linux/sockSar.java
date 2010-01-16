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
public class sockSar extends AllGraph {

    public sockSar(kSar hissar) {
        super(hissar);
        Title = new String("Sockets");
        t_totsck = new TimeSeries("totsck", org.jfree.data.time.Second.class);
        mysar.dispo.put("Total Socket", t_totsck);
        t_tcpsck = new TimeSeries("tcpsck", org.jfree.data.time.Second.class);
        mysar.dispo.put("TCP socket", t_tcpsck);
        t_udpsck = new TimeSeries("udpsck", org.jfree.data.time.Second.class);
        mysar.dispo.put("UDP socket", t_udpsck);
        t_rawsck = new TimeSeries("rawsck", org.jfree.data.time.Second.class);
        mysar.dispo.put("RAW socket", t_rawsck);
        t_ipfrag = new TimeSeries("ip-frag", org.jfree.data.time.Second.class);
        mysar.dispo.put("IP Frag", t_ipfrag);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5) {
        this.t_totsck.add(now, val1);
        this.t_tcpsck.add(now, val2);
        this.t_udpsck.add(now, val3);
        this.t_rawsck.add(now, val4);
        this.t_ipfrag.add(now, val5);
        number_of_sample++;
    }

    public XYDataset createtotsck() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_totsck);
        return graphcollection;
    }

    public XYDataset createsck() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_tcpsck);
        graphcollection.addSeries(this.t_udpsck);
        graphcollection.addSeries(this.t_rawsck);
        return graphcollection;
    }

    public XYDataset createipfrag() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_ipfrag);
        return graphcollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXSOCK", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        CombinedDomainXYPlot plot = null;
        // tps
        XYDataset xydataset1 = this.createtotsck();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("totsck"), minichart1);
        // rws
        XYDataset sck = this.createsck();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setSeriesPaint(1, kSarConfig.color3);
        minichart2.setSeriesPaint(2, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(sck, null, new NumberAxis("udp/tcp/raw"), minichart2);
        // nice 
        XYDataset ipfrag = this.createipfrag();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color5);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(ipfrag, null, new NumberAxis("ip-frag"), minichart3);
        // the graph
        plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot3, 1);
        plot.add(subplot2, 1);
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
            subplot3.setBackgroundPaint(null);
        }
        return mychart;
    }
    private TimeSeries t_totsck;
    private TimeSeries t_tcpsck;
    private TimeSeries t_udpsck;
    private TimeSeries t_rawsck;
    private TimeSeries t_ipfrag;
    private String ioOpt = new String("");
}
