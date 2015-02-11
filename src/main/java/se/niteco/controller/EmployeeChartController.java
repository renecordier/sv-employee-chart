package se.niteco.controller;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * Public class of the chart portlet
 */
@Controller
@RequestMapping(value="VIEW")
public class EmployeeChartController {
	@Autowired
	private static VelocityEngine velocityEngine;
	
	@RenderMapping
	public String showChart(Model model, RenderRequest request, RenderResponse response){
        return "chart";
	}
}
