/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Solaris;

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

/**
 *
 * @author alex
 */
public class syscallSar extends AllGraph {

    public syscallSar(final kSar hissar) {
        super(hissar);
        Title = "Syscalls";
        t_scall = new TimeSeries("Syscall/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Syscall/s", t_scall);
        t_sread = new TimeSeries("Read/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Read/s", t_sread);
        t_swrit = new TimeSeries("Write/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Write/s", t_swrit);
        t_fork = new TimeSeries("Fork/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Fork/s", t_fork);
        t_exec = new TimeSeries("Exec/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Exec/s", t_exec);
        t_rchar = new TimeSeries("Rchar/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Rchar/s", t_rchar);
        t_wchar = new TimeSeries("Wchar/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Wchar/s", t_wchar);
        // collection
        rw_collection = new TimeSeriesCollection();
        rw_collection.addSeries(this.t_sread);
        rw_collection.addSeries(this.t_swrit);
        call_collection = new TimeSeriesCollection();
        call_collection.addSeries(this.t_scall);
        fork_collection = new TimeSeriesCollection();
        fork_collection.addSeries(this.t_fork);
        fork_collection.addSeries(this.t_exec);
        char_collection = new TimeSeriesCollection();
        char_collection.addSeries(this.t_rchar);
        char_collection.addSeries(this.t_wchar);
    }

    public void add(final Second now,final  Float val1Init,final  Float val2Init,final  Float val3Init,final  Float val4Init,final  Float val5Init,final  Float val6Init,final  Float val7Init) {
        this.t_scall.add(now, val1Init, do_notify());
        this.t_sread.add(now, val2Init, do_notify());
        this.t_swrit.add(now, val3Init, do_notify());
        this.t_fork.add(now, val4Init, do_notify());
        this.t_exec.add(now, val5Init, do_notify());
        this.t_rchar.add(now, val6Init, do_notify());
        this.t_wchar.add(now, val7Init, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "SOLARISSYSCALL", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        // rw
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(rw_collection, null, new NumberAxis("read/write /s"), minichart1);
        // call
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(call_collection, null, new NumberAxis("syscall/s"), minichart2);
        // fork
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color4);
        minichart3.setSeriesPaint(1, kSarConfig.color5);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(fork_collection, null, new NumberAxis("fork/exec /s"), minichart3);
        // char
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color6);
        minichart4.setSeriesPaint(1, kSarConfig.color7);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(char_collection, null, new NumberAxis("rchar/wchar /s"), minichart4);

        // PANEL
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.add(subplot4, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        mygraph = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mygraph) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
            subplot3.setBackgroundPaint(null);
            subplot4.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mygraph.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }

        return mygraph;

    }
    private final TimeSeries t_scall;
    private final TimeSeries t_sread;
    private final TimeSeries t_swrit;
    private final TimeSeries t_fork;
    private final TimeSeries t_exec;
    private final TimeSeries t_rchar;
    private final TimeSeries t_wchar;
    private final TimeSeriesCollection rw_collection;
    private final TimeSeriesCollection call_collection;
    private final TimeSeriesCollection fork_collection;
    private final TimeSeriesCollection char_collection;
}
