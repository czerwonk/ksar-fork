/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;

import java.awt.Color;
import java.text.NumberFormat;
import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.IEEE1541Number;
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
public class kbmiscSar extends AllGraph {

    public kbmiscSar(kSar hissar) {
        super(hissar);
        Title = new String("Memory Misc");
        t_shrd = new TimeSeries("memshrd", org.jfree.data.time.Second.class);
        mysar.dispo.put("Memory shared", t_shrd);
        t_buff = new TimeSeries("buffers", org.jfree.data.time.Second.class);
        mysar.dispo.put("Memory buffers", t_buff);
        t_cach = new TimeSeries("cached", org.jfree.data.time.Second.class);
        mysar.dispo.put("Memory cached", t_cach);
        t_used_bufferadj = new TimeSeries("used(buffer adjusted)", org.jfree.data.time.Second.class);
        mysar.dispo.put("Memory used (buffer adjusted)", t_used_bufferadj);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4) {
        this.t_shrd.add(now, val1.doubleValue() * 1024, do_notify());
        this.t_buff.add(now, val2.doubleValue() * 1024, do_notify());
        this.t_cach.add(now, val3.doubleValue() * 1024, do_notify());
        this.t_used_bufferadj.add(now, val4.doubleValue() * 1024, do_notify());
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2, Float val3) {
        this.t_shrd.add(now, val1.doubleValue() * 1024, do_notify());
        this.t_buff.add(now, val2.doubleValue() * 1024, do_notify());
        this.t_cach.add(now, val3.doubleValue() * 1024, do_notify());
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2) {
        this.t_buff.add(now, val1.doubleValue() * 1024, do_notify());
        this.t_cach.add(now, val2.doubleValue() * 1024, do_notify());
        number_of_sample++;
    }
    
    public void addused_bufferadj(Second now, Float v) {
        this.t_used_bufferadj.add(now, v.doubleValue() * 1024);
    }

    public XYDataset createbuff() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_buff);
        return graphcollection;
    }

    public XYDataset createcach() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_cach);
        return graphcollection;
    }

    public XYDataset createshrd() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_shrd);
        return graphcollection;
    }

    public XYDataset createused_bufferadj() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_used_bufferadj);
        return graphcollection;
    }

    public void setmiscOpt(String s) {
        this.miscOpt = s;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXKBMISC", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // used
        XYDataset buffset = this.createbuff();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        NumberAxis numberaxis = new NumberAxis("buffers");
        NumberFormat decimalformat = new IEEE1541Number(1);
        numberaxis.setNumberFormatOverride(decimalformat);

        XYPlot subplot1 = new XYPlot(buffset, null, numberaxis, minichart1);
        // idle
        XYDataset cachset = this.createcach();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        NumberAxis numberaxis1 = new NumberAxis("cached");
        numberaxis1.setNumberFormatOverride(decimalformat);

        XYPlot subplot2 = new XYPlot(cachset, null, numberaxis1, minichart2);
        //
        XYPlot subplot3 = null;
        if (miscOpt.equals("kbmemshrd")) {
            XYDataset shrdset = this.createshrd();
            XYItemRenderer minichart3 = new StandardXYItemRenderer();
            minichart3.setSeriesPaint(0, kSarConfig.color3);
            minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            NumberAxis numberaxis2 = new NumberAxis("memshrd");
            numberaxis2.setNumberFormatOverride(decimalformat);

            subplot3 = new XYPlot(shrdset, null, numberaxis2, minichart3);
        }
        // Memory used - cached - buffers (Memory use by apps and system)
        XYDataset memused_bufferadj = this.createused_bufferadj();
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color4);
        NumberAxis numberaxis4 = new NumberAxis("memused (buffer adjusted)");
        numberaxis4.setNumberFormatOverride(decimalformat);

        XYPlot subplot4 = new XYPlot(memused_bufferadj, null, numberaxis4, minichart4);
        //
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        if (miscOpt.equals("kbswpcad")) {
            plot.add(subplot3, 1);
        }
        if (mysar.showmemusedbuffersadjusted) {
            plot.add(subplot4, 1);
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
            if (miscOpt.equals("kbswpcad")) {
                subplot3.setBackgroundPaint(null);
            }
        }
        return mychart;
    }
    private TimeSeries t_shrd;
    private TimeSeries t_buff;
    private TimeSeries t_cach;
    private TimeSeries t_used_bufferadj;
    private String miscOpt = new String("");
}
