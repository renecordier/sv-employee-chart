package se.niteco.controller;

import java.util.Arrays;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.DataUtil;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;

/**
 * Public class of the chart portlet
 */
@Controller
@RequestMapping(value="VIEW")
public class EmployeeChartController {
	@Autowired
	private static VelocityEngine velocityEngine;
	
	@RenderMapping
	public String showChart(Model model, RenderRequest request, RenderResponse response, PortletPreferences pref){
		final int MAX_NUMBER = 50;
		Data ageData= DataUtil.scaleWithinRange(0, MAX_NUMBER, Arrays.asList(15, 28, 7, 10, 1));
		BarChartPlot team1 = Plots.newBarChartPlot(ageData, Color.ORANGE, "Niteco employees");
        //BarChartPlot team2 = Plots.newBarChartPlot(Data.newData(8, 35, 11, 5), Color.ORANGERED, "Team B");
        //BarChartPlot team3 = Plots.newBarChartPlot(Data.newData(10, 20, 30, 30), Color.LIMEGREEN, "Team C");

        // Instantiating chart.
        BarChart chart = GCharts.newBarChart(team1);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.BLACK, 13, AxisTextAlignment.CENTER);
        AxisLabels number = AxisLabelsFactory.newAxisLabels("Number", 50.0);
        number.setAxisStyle(axisStyle);
        AxisLabels age = AxisLabelsFactory.newAxisLabels("Age range", 50.0);
        age.setAxisStyle(axisStyle);

        // Adding axis info to chart.
        chart.addXAxisLabels(AxisLabelsFactory.newAxisLabels("18 - 20", "21 - 30", "31 - 40", "41 - 50", "Over 50"));
        chart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, MAX_NUMBER));
        chart.addYAxisLabels(number);
        chart.addXAxisLabels(age);

        chart.setSize(500, 300);
        chart.setBarWidth(50);
        chart.setSpaceWithinGroupsOfBars(30);
        //chart.setDataStacked(true);
        chart.setTitle("Age statistics at Niteco", Color.BLACK, 16);
        chart.setGrid(100, 10, 3, 2);
        chart.setBackgroundFill(Fills.newSolidFill(Color.ALICEBLUE));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.LAVENDER, 100);
        fill.addColorAndOffset(Color.WHITE, 0);
        chart.setAreaFill(fill);
        String url = chart.toURLString();
        
        model.addAttribute("chartUrl", url);
        
        return "chart";
	}
}
