package com.reliaquest.api.controller;

import com.reliaquest.api.annotation.RateLimit;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final EmployeeService employeeService;

    @Override
    @RateLimit(requests = 20, windowSeconds = 60, key = "ip", message = "Too many requests for getAllEmployees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("Getting all employees");
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Override
    @RateLimit(requests = 30, windowSeconds = 60, key = "ip", message = "Too many search requests")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        log.info("Searching employees by name: {}", searchString);
        List<Employee> employees = employeeService.searchEmployeesByName(searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    @RateLimit(requests = 50, windowSeconds = 60, key = "ip", message = "Too many requests for getEmployeeById")
    public ResponseEntity<Employee> getEmployeeById(String id) {
        log.info("Getting employee by id: {}", id);
        Employee employee = employeeService.getEmployeeByIdOrThrow(id);
        return ResponseEntity.ok(employee);
    }

    @Override
    @RateLimit(requests = 40, windowSeconds = 60, key = "ip", message = "Too many requests for salary information")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Getting highest salary of employees");
        Integer highestSalary = employeeService.getHighestSalary();
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    @RateLimit(requests = 40, windowSeconds = 60, key = "ip", message = "Too many requests for top earners")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("Getting top 10 highest earning employee names");
        List<String> topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.ok(topTenNames);
    }

    @Override
    @RateLimit(requests = 10, windowSeconds = 60, key = "ip", message = "Too many employee creation requests")
    public ResponseEntity<Employee> createEmployee(@Valid EmployeeInput employeeInput) {
        log.info("Creating employee: {}", employeeInput.getName());
        Employee createdEmployee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @Override
    @RateLimit(requests = 5, windowSeconds = 60, key = "ip", message = "Too many employee deletion requests")
    public ResponseEntity<String> deleteEmployeeById(String id) {
        log.info("Deleting employee by id: {}", id);
        String deletedEmployeeName = employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok(deletedEmployeeName);
    }
}
