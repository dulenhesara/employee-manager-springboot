package com.example.employee_manager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> searchEmployees(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return employeeRepository.findByFullNameContainingIgnoreCase(keyword.trim());
        }
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id: " + id));
    }

    public void saveEmployee(Employee employee) {
        Optional<Employee> existingEmployee = employeeRepository.findByEmail(employee.getEmail());
        if (existingEmployee.isPresent() && !existingEmployee.get().getId().equals(employee.getId())) {
            throw new IllegalStateException("This email is already registered");
        }
        employeeRepository.save(employee);
    }

    public void updateEmployee(Long id, Employee updatedEmployee) {
        Employee existing = getEmployeeById(id);

        Optional<Employee> emailOwner = employeeRepository.findByEmail(updatedEmployee.getEmail());
        if (emailOwner.isPresent() && !emailOwner.get().getId().equals(id)) {
            throw new IllegalStateException("This email is already registered");
        }

        existing.setFullName(updatedEmployee.getFullName());
        existing.setEmail(updatedEmployee.getEmail());
        existing.setPhone(updatedEmployee.getPhone());
        existing.setDepartment(updatedEmployee.getDepartment());
        existing.setSalary(updatedEmployee.getSalary());

        employeeRepository.save(existing);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}