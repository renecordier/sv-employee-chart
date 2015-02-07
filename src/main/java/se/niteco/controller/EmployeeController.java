package se.niteco.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import se.niteco.jms.AgeSender;
import se.niteco.jms.AgeSenderImpl;
import se.niteco.model.Employee;
import se.niteco.service.EmployeeService;
import se.niteco.service.EmployeeServiceImpl;
import senselogic.sitevision.api.Utils;
import senselogic.sitevision.api.context.PortletContextUtil;
import senselogic.sitevision.api.metadata.MetadataUtil;
import senselogic.sitevision.api.property.PropertyUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EmployeeController {
	@Autowired
	@Qualifier("employeeService")
	protected EmployeeService service; //DI of employee service
	
	@Autowired
	@Qualifier("ageSender")
	private static AgeSender ageSender; //DI of age sender
	
	protected final static String META_EMPLOYEES_HR = "employeesHR";//metadata name 
	protected final static String META_EMPLOYEES_DEV = "employeesDev";//metadata name
	
	protected final Gson gson = new Gson();
	
    private final Type employeesType =  new TypeToken<ArrayList<Employee>>() {}.getType();
	
	//DI of velocity engine
	private VelocityEngine velocityEngine; 
    
    /**
     * @param velocityEngine the velocityEngine to set
     */
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    /**
     * @return the velocityEngine
     */
    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }
    
    /**
     * Loading the list of employees from the metadata
     * @param request
     */
	protected void loadEmployeesList(PortletRequest request, PortletPreferences pref) { 
		if(service == null)
			service = new EmployeeServiceImpl();
		
		String employeesJSON = null;
		boolean init = false;
		
		String employeeList = pref.getValue("listEmployee", "setup");
		if (employeeList == null || employeeList.equals("setup") || employeeList.trim().equalsIgnoreCase("")) {
			String mode = pref.getValue("mode", "");
			
			String meta = META_EMPLOYEES_HR;
			if (mode.equals("Dev"))
				meta = META_EMPLOYEES_DEV;
			
			Utils utils = (Utils)request.getAttribute("sitevision.utils");
	        PortletContextUtil pcUtil = utils.getPortletContextUtil();
	        PropertyUtil propertyUtil = utils.getPropertyUtil();
	        
	        Node currentPage = pcUtil.getCurrentPage();
	       
	        employeesJSON = propertyUtil.getString(currentPage, meta);
	        
	        try {
				pref.setValue("listEmployee", employeesJSON);
				pref.store();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        init = true;
		} else {
			employeesJSON = employeeList;
		}
		
		if (employeesJSON != null && employeesJSON.trim().length() > 0) {
            try {
            	service.setEmployees((List<Employee>) gson.fromJson(employeesJSON, employeesType));
            	if (init)
            		sendData(pref);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		 
	}
	
	/**
	 * Saving the list of employees in the metadata
	 * @param request
	 * @throws Exception
	 */
	protected void saveEmployeesList(PortletRequest request, PortletPreferences pref) throws Exception {
		String mode = pref.getValue("mode", "View");
        Utils utils = (Utils)request.getAttribute("sitevision.utils");
        PortletContextUtil pcUtil = utils.getPortletContextUtil();
        MetadataUtil metaUtil = utils.getMetadataUtil();
        Node currentPage = pcUtil.getCurrentPage();
        
        String meta = META_EMPLOYEES_HR;
		if (mode.equals("Dev"))
			meta = META_EMPLOYEES_DEV;
		
		String employeesJSON = gson.toJson(service.getEmployees());
        
        metaUtil.setMetadataPropertyValue(currentPage, meta, employeesJSON);
        try {
			pref.setValue("listEmployee", employeesJSON);
			pref.store();
			sendData(pref);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
	
	protected void sendData(PortletPreferences pref) {
		List<Integer> ages = new LinkedList<Integer>(Arrays.asList(0,0,0,0,0,0));
		
		String mode = pref.getValue("mode", "");
		if(mode.equals("Dev"))
			ages.set(0, 1);
		
		for(Employee emp : service.getEmployees()) {
			int age = emp.getAge();
			if (age < 21)
				ages.set(1, (ages.get(1) + 1));
			else if(age < 31)
				ages.set(2, (ages.get(2) + 1));
			else if(age < 41)
				ages.set(3, (ages.get(3) + 1));
			else if(age < 51)
				ages.set(4, (ages.get(4) + 1));
			else
				ages.set(5, (ages.get(5) + 1));
		}
		
		if(ageSender == null)
			ageSender = new AgeSenderImpl();
		ageSender.sendEmployeeAges(ages);
	}
	    
}
