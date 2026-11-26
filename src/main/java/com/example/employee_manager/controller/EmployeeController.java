package com.example.employee_manager.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;

import jakarta.validation.Valid;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Redirect root URL http://localhost:8081/ directly to /employees
    @GetMapping("/")
    public String home() {
        return "redirect:/employees";
    }

    // READ: Show the registration form, search bar, and employee table
    @GetMapping("/employees")
    public String listEmployees(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Employee> employees = employeeService.searchEmployees(keyword);
        if (!model.containsAttribute("employee")) {
            model.addAttribute("employee", new Employee());
        }
        model.addAttribute("employees", employees);
        model.addAttribute("keyword", keyword);
        return "employees";
    }

    // CREATE: Save a new employee
    @PostMapping("/employees")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "employees";
        }

        try {
            employeeService.saveEmployee(employee);
            redirectAttributes.addFlashAttribute("successMessage", "Employee added successfully!");
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("email", "error.employee", e.getMessage());
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "employees";
        }

        return "redirect:/employees";
    }

    // SHOW EDIT FORM: Load existing employee data into editemployee.html
    @GetMapping("/employees/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "editemployee";
    }

    // UPDATE: Save edited employee details
    @PostMapping("/employees/{id}/update")
    public String updateEmployee(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            employee.setId(id);
            return "editemployee";
        }

        try {
            employeeService.updateEmployee(id, employee);
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!");
        } catch (IllegalStateException e) {
            employee.setId(id);
            bindingResult.rejectValue("email", "error.employee", e.getMessage());
            return "editemployee";
        }

        return "redirect:/employees";
    }

    // DELETE: Remove an employee record
    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        employeeService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!");
        return "redirect:/employees";
    }
}