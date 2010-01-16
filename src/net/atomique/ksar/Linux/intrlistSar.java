// ksar
// cleanup
package net.atomique.ksar.Linux;

import java.awt.Color;
import org.jfree.data.time.Second;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.axis.DateAxis;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JTree;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.kSar;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;

public class intrlistSar extends AllGraph {

    public intrlistSar(kSar hissar, String intrId) {
        super(hissar);
        this.intrId = intrId;
        Title = new String("Interrupt " + intrId);
        stacked_intr = new TimeTableXYDataset();
    }

    public void add(Second now, String cpu, Float val1) {
        stacked_intr.add(now, val1.doubleValue(), cpu);
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXINTRLIST", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        XYPlot subplot1;
        NumberAxis intraxis = new NumberAxis("intr/s");
        if (mysar.showstackedintrlist == false) {
            XYItemRenderer minichart1 = new StandardXYItemRenderer();
            subplot1 = new XYPlot(stacked_intr, null, intraxis, minichart1);
        } else {
            StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
            subplot1 = new XYPlot(stacked_intr, new DateAxis(null), intraxis, renderer);
        }

        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 2);
        // the graph
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        mychart.setBackgroundPaint(Color.white);
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
        }

        return mychart;
    }
    private TimeTableXYDataset stacked_intr;
    private String intrId;
}
