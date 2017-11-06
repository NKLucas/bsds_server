package edu.neu.zhiyu.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChartMaker extends JFrame{
    private XYSeriesCollection dataSet;

    public ChartMaker() {
        dataSet = new XYSeriesCollection();
    }

    public void makeChart(List<Long> data, String name) throws IOException {
        XYSeries series = new XYSeries(name);
        for (int i = 0; i < data.size(); i++) {
            series.add(i, data.get(i));
        }
        dataSet.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(name, "Count", "Latency", dataSet);

        File file = new File(name + ".jpeg");
        ChartUtilities.saveChartAsJPEG(file, chart, 800, 600);
    }


}
