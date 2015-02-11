package se.niteco.controller;

import javax.portlet.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.niteco.model.Employee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Portlet class of Niteco's employees
 */
@Controller("employeePortlet")
@RequestMapping(value="VIEW")
public class EmployeeViewController extends EmployeeController {
	private Map<String, String> cssCheckingMap;//error messages when adding or editing an employee
	private Map<String, String> valuesMap;//keeping values to show in add or edit employee
	
	/**
	 * Default view mode to show the list of employees
	 * @param model
	 * @param request
	 * @param response
	 * @param pref
	 * @return
	 */
	@RenderMapping
	public String showEmployee(Model model, RenderRequest request, RenderResponse response, PortletPreferences pref){
		//Get list of employee
		loadEmployeesList(request, pref);
		
		//Set add url
		PortletURL showAddUrl = response.createRenderURL();
		showAddUrl.setParameter("action", "showAdd");
		model.addAttribute("showAddUrl", showAddUrl);

		//Set edit url
		PortletURL editUrl = response.createRenderURL();
		editUrl.setParameter("action", "showEdit");
		model.addAttribute("editUrl", editUrl);

		//Set remove url
		PortletURL removeUrl = response.createActionURL();
		removeUrl.setParameter("action", "deleteEmployee");
		model.addAttribute("removeUrl", removeUrl);
		
		//set edit pic url
		PortletURL editPicUrl = response.createRenderURL();
		editPicUrl.setParameter("action", "editPic");
		model.addAttribute("editPicUrl", editPicUrl);
		
      	List<Employee> lst = service.getEmployees();
      	model.addAttribute("employees", lst);
      	model.addAttribute("request", request);
      	String mode = pref.getValue("mode", "");
		model.addAttribute("mode", mode);
		return "listEmployee";
	}
	
	/**
	 * View for adding an employee to the list
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RenderMapping(params = "action=showAdd")
	public String showAdd(Model model, RenderRequest request, RenderResponse response, PortletPreferences pref){
		//Get list of employee
		loadEmployeesList(request, pref);
		
		//Set url to model
		PortletURL actionUrl = response.createActionURL();
		actionUrl.setParameter("action", "insertEmployee");
		
		//Set cancel url
		PortletURL cancelUrl = response.createActionURL();
		cancelUrl.setParameter("action", "cancel");
		
		model.addAttribute("actionUrl", actionUrl);
		model.addAttribute("cancelUrl", cancelUrl);
		
		model.addAttribute("addPage", true);
		model.addAttribute("checkings", cssCheckingMap);
		model.addAttribute("employee", valuesMap);
		
		//get list of cities
      	model.addAttribute("request", request);
		
		return "addEditEmployee";
	}
	
	/**
	 * Action for adding an employee to the list
	 * @param request
	 * @param response
	 */
	@ActionMapping(params = "action=insertEmployee")
	public void doAdd(ActionRequest request, ActionResponse response, PortletPreferences pref){
		String id = request.getParameter("employeeId");
		String name = request.getParameter("employeeName");
		String email = request.getParameter("employeeEmail");
		String team = request.getParameter("employeeTeam");
		String role = request.getParameter("employeeRole");
		String salary = request.getParameter("employeeSalary");
		String age = request.getParameter("employeeAge");
		
		boolean error = false;
		
		loadEmployeesList(request, pref);
		
		cssCheckingMap = new HashMap<String, String>();
		if (name == null || name.trim().equalsIgnoreCase("")) {
			cssCheckingMap.put("name", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("name", "has-success");
		}
		if (email == null || email.trim().equalsIgnoreCase("")) {
			cssCheckingMap.put("email", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("email", "has-success");
		}
		if (team == null || team.trim().equalsIgnoreCase("")) {
			cssCheckingMap.put("team", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("team", "has-success");
		}
		if (role == null || role.trim().equalsIgnoreCase("")) {
			cssCheckingMap.put("role", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("role", "has-success");
		}
		if (salary == null || salary.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(salary)) {
			cssCheckingMap.put("salary", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("salary", "has-success");
		}
		if (age == null || age.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(age)) {
			cssCheckingMap.put("age", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("age", "has-success");
		}
		if (id == null || id.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(id) || !service.isIdUnique(Integer.parseInt(id))) {
			cssCheckingMap.put("id", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("id", "has-success");
		}
		
		valuesMap = new HashMap<String, String>();
		
		if (!error) {
			service.addEmployee(new Employee(Integer.parseInt(id), name, email, team, role, Integer.parseInt(salary), Integer.parseInt(age)));
			try {
				saveEmployeesList(request, pref);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// contains property name to property value map, for re-rendering
			// the form with values that were entered by the user for each form field
			valuesMap.put("name", name);
			valuesMap.put("email", email);
			valuesMap.put("team", team);
			valuesMap.put("role", role);
			valuesMap.put("salary", salary);
			valuesMap.put("age", age);
			valuesMap.put("id", id);
			response.setRenderParameter("action", "showAdd");
		}	
	}
	
	/**
	 * View for editing en employee
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RenderMapping(params = "action=showEdit")
	public String showEdit(Model model, RenderRequest request, RenderResponse response, PortletPreferences pref){
		//Get list of employee
		loadEmployeesList(request, pref);
		
		//Set url to model
		PortletURL actionUrl = response.createActionURL();
		actionUrl.setParameter("action", "updateEmployee");
		
		//Set cancel url
		PortletURL cancelUrl = response.createActionURL();
		cancelUrl.setParameter("action", "cancel");
				
		model.addAttribute("actionUrl", actionUrl);
		model.addAttribute("cancelUrl", cancelUrl);
		
		//Get selected employee
		String employeeId = request.getParameter("employeeId");
		if (employeeId != null) {
			int id = Integer.parseInt(employeeId);
			Employee employee = service.getEmployee(id);
			valuesMap = new HashMap<String, String>();
			valuesMap.put("name", employee.getName());
			valuesMap.put("email", employee.getEmail());
			valuesMap.put("team", employee.getTeam());
			valuesMap.put("role", employee.getRole());
			valuesMap.put("salary", employee.getSalary()+"");
			valuesMap.put("id", employee.getId()+"");
			valuesMap.put("age", employee.getAge()+"");
		}
		
		model.addAttribute("addPage", false);
		model.addAttribute("checkings", cssCheckingMap);
		model.addAttribute("employee", valuesMap);
		
      	model.addAttribute("request", request);
		
		return "addEditEmployee";
	}
	
	/**
	 * Action for updating an employee in the list
	 * @param request
	 * @param response
	 */
	@ActionMapping(params = "action=updateEmployee")
	public void doEdit(ActionRequest request, ActionResponse response, PortletPreferences pref){
		String id = request.getParameter("employeeId");
		String name = request.getParameter("employeeName");
		String email = request.getParameter("employeeEmail");
		String team = request.getParameter("employeeTeam");
		String role = request.getParameter("employeeRole");
		String salary = request.getParameter("employeeSalary");
		String age = request.getParameter("employeeAge");
		
		boolean error = false;
		
		loadEmployeesList(request, pref);
		
		cssCheckingMap = new HashMap<String, String>();
		if (name == null || name.trim().equalsIgnoreCase("")) {
			cssCheckingMap.put("name", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("name", "has-success");
		}
		if (email == null || email.trim().equalsIgnoreCase("")) {
			cssCheckingMap.put("email", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("email", "has-success");
		}
		if (team == null || team.trim().equalsIgnoreCase("")) {
			cssCheckingMap.put("team", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("team", "has-success");
		}
		if (role == null || role.trim().equalsIgnoreCase("")) {
			cssCheckingMap.put("role", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("role", "has-success");
		}
		if (salary == null || salary.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(salary)) {
			cssCheckingMap.put("salary", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("salary", "has-success");
		}
		if (age == null || age.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(age)) {
			cssCheckingMap.put("age", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("age", "has-success");
		}
		if (id == null || id.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(id)) {
			cssCheckingMap.put("id", "has-error");
			error = true;
		} else {
			cssCheckingMap.put("id", "has-success");
		}
		
		valuesMap = new HashMap<String, String>();
		
		if (!error) {
			service.updateEmployee(new Employee(Integer.parseInt(id), name, email, team, role, Integer.parseInt(salary), Integer.parseInt(age)));
			try {
				saveEmployeesList(request, pref);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// contains property name to property value map, for re-rendering
			// the form with values that were entered by the user for each form field
			valuesMap.put("name", name);
			valuesMap.put("email", email);
			valuesMap.put("team", team);
			valuesMap.put("role", role);
			valuesMap.put("salary", salary);
			valuesMap.put("age", age);
			valuesMap.put("id", id);
			response.setRenderParameter("action", "showEdit");
		}		
	}
	
	/**
	 * Action of deleting an employee
	 * @param request
	 */
	@ActionMapping(params = "action=deleteEmployee")
	public void doRemove(ActionRequest request, PortletPreferences pref){
		String id = request.getParameter("employeeId");
		if (id != null) { //delete action
			loadEmployeesList(request, pref);
			service.removeEmployee(Integer.parseInt(id));
			try {
				saveEmployeesList(request, pref);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Cancel action
	 * @param request
	 */
	@ActionMapping(params = "action=cancel")
	public void doCancel(ActionRequest request){
		cssCheckingMap = new HashMap<String, String>();
		valuesMap = new HashMap<String, String>();
	}
}
