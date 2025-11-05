package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        List<Employee> employees = employeeService.searchEmployeesByName(searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        Employee employee = employeeService.getEmployeeByIdOrThrow(id);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = employeeService.getHighestSalary();
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.ok(topTenNames);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid EmployeeInput employeeInput) {
        Employee createdEmployee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        String deletedEmployeeName = employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok(deletedEmployeeName);
    }
}
