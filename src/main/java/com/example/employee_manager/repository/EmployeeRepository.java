package com.example.employee_manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.employee_manager.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Used to check for duplicate emails during create/update
    Optional<Employee> findByEmail(String email);

    // Used for Task 11 Challenge: Search employees by name
    List<Employee> findByFullNameContainingIgnoreCase(String keyword);
}