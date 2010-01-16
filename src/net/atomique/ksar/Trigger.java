/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.ui.Layer;

/**
 *
 * @author alex
 */
// ksar
public class Trigger {

    public Trigger(kSar hissar, AllGraph hisgraph, TimeSeries hisseries, String hisway) {
        mysar = hissar;
        mygraph = hisgraph;
        myseries = hisseries;
        myway = hisway;
    }

    public Trigger(kSar hissar, AllGraph hisgraph, String s, TimeSeries hisseries, String hisway) {
        mysar = hissar;
        mygraph = hisgraph;
        comment = s;
        myseries = hisseries;
        myway = hisway;
    }

    public void doMarker(Second now, Number v) {
        if ("up".equals(myway)) {
            doMarkerUP(now, v);
        }
        if ("down".equals(myway)) {
            doMarkerDOWN(now, v);
        }
    }

    public void doclose() {
        if ("up".equals(myway)) {
            closeup();
        }
        if ("down".equals(myway)) {
            closedown();
        }
    }

    private void doMarkerUP(Second now, Number v) {
        lasttriggervalue = triggervalue;
        //Double val = new Double(v.doubleValue());
        // if value is more than trigger
        if (v.doubleValue() >= triggervalue.doubleValue()) {
            // first time ?
            if (debutmarker == null) {
                debutmarker = now;
                finmarker = now;
            } else {
                // no make end now so
                finmarker = now;
            }
        } else {
            // ok we came back but trigger was up
            if (debutmarker == null) {
                debutmarker = null;
                finmarker = null;
                return;
            } else {
                closeup();
            }
        }
    }

    private void closeup() {
        if (debutmarker != finmarker) {
            IntervalMarker intervalobj = new IntervalMarker(debutmarker.getFirstMillisecond(), finmarker.getLastMillisecond());
            intervalobj.setPaint(new Color(200, 200, 200));
            WarningList.add(intervalobj);
            mysar.DetectedBounds.put(mygraph.getcheckBoxTitle() + " " + comment + " over " + triggervalue, mygraph);
        }
        debutmarker = null;
        finmarker = null;
    }

    private void doMarkerDOWN(Second now, Number v) {
        lasttriggervalue = triggervalue;
        //Double val = new Double(v.doubleValue());
        if (v.doubleValue() <= triggervalue.doubleValue()) {
            if (debutmarker == null) {
                debutmarker = now;
                finmarker = now;
            } else {
                finmarker = now;
            }
        } else {
            closedown();
        }
    }

    private void closedown() {
        if (debutmarker != finmarker) {
            IntervalMarker intervalobj = new IntervalMarker(debutmarker.getFirstMillisecond(), finmarker.getLastMillisecond());
            intervalobj.setPaint(new Color(200, 200, 200));
            WarningList.add(intervalobj);
            mysar.DetectedBounds.put(mygraph.getcheckBoxTitle() + " " + comment + " under " + triggervalue, mygraph);
        }
        debutmarker = null;
        finmarker = null;
    }

    public void tagMarker(XYPlot myplot) {
        if (! mysar.showtrigger ) {
            return;
        }
        if (WarningList.size() > 0) {
            ListIterator<IntervalMarker> listItr = WarningList.listIterator();
            while (listItr.hasNext()) {
                IntervalMarker value = listItr.next();
                myplot.addDomainMarker(value, Layer.BACKGROUND);
            }
        }
    }

    private void refresh() {
        if ( ! mysar.showtrigger ) {
            return;
        }
        if (lasttriggervalue.doubleValue() == triggervalue.doubleValue()) {
            return;
        }
        debutmarker = null;
        finmarker = null;
        mysar.DetectedBounds.remove(mygraph.getcheckBoxTitle() + " " + comment + " under " + lasttriggervalue);
        mysar.DetectedBounds.remove(mygraph.getcheckBoxTitle() + " " + comment + " over " + lasttriggervalue);
        lasttriggervalue = triggervalue;
        WarningList.clear();
        int count = myseries.getItemCount();
        for (int i = 0; i < count; i++) {
            tmpnum = myseries.getValue(i).doubleValue();
            tmpsec = new Second(myseries.getTimePeriod(i).getStart());
            if (myway.equals("down")) {
                if (tmpnum <= triggervalue.doubleValue()) {
                    if (debutmarker == null) {
                        debutmarker = tmpsec;
                        finmarker = tmpsec;
                    } else {
                        finmarker = tmpsec;
                    }
                } else {
                    if (debutmarker == null) {
                        debutmarker = null;
                        finmarker = null;
                        continue;
                    } else {
                        closedown();
                    }
                }
            }
            if (myway.equals("up")) {
                if (tmpnum >= triggervalue.doubleValue()) {
                    if (debutmarker == null) {
                        debutmarker = tmpsec;
                        finmarker = tmpsec;
                    } else {
                        finmarker = tmpsec;
                    }
                } else {
                    if (debutmarker == null) {
                        debutmarker = null;
                        finmarker = null;
                        continue;
                    } else {
                        closeup();
                    }
                }
            }

        }
        // end test
        if (myway.equals("down")) {
            if (debutmarker == null) {
                debutmarker = null;
                finmarker = null;
            } else {
                closedown();
            }
        }
        if (myway.equals("up")) {
            if (debutmarker == null) {
                debutmarker = null;
                finmarker = null;
            } else {
                closeup();
            }
        }
    }

    public void setComment(String s) {
        comment = s;
    }

    public void setTriggerValue(double d) {
        triggervalue = new Double(d);
        refresh();
    }
    public void setTriggerValue(Double d) {
        triggervalue = d;
        refresh();
    }
    
    Double triggervalue = new Double(0);
    kSar mysar;
    AllGraph mygraph;
    TimeSeries myseries;
    double tmpnum;
    String myway = new String("none");
    Double lasttriggervalue = new Double(0);
    Second tmpsec;
    Second debutmarker = null;
    Second finmarker = null;
    String comment = "";
    List<IntervalMarker> WarningList = new ArrayList<IntervalMarker>();
}
