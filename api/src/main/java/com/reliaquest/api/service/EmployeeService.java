package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.constants.EmployeeConstants;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.validator.EmployeeValidator;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeApiClient apiClient;
    private final EmployeeValidator validator;

    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");
        List<Employee> employees = apiClient.getAllEmployees();
        log.info("Successfully fetched {} employees", employees.size());
        return employees;
    }

    public Employee createEmployee(EmployeeInput employeeInput) {
        return apiClient.createEmployee(employeeInput);
    }
    
    public List<Employee> searchEmployeesByName(String searchString) {
        validator.validateSearchString(searchString);
        log.info("Searching employees by name: {}", searchString);
        
        try {
            List<Employee> allEmployees = getAllEmployees();
            List<Employee> filteredEmployees = allEmployees.stream()
                    .filter(employee -> employee.getEmployeeName() != null
                            && employee.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                    .collect(Collectors.toList());

            log.info("Found {} employees matching search: {}", filteredEmployees.size(), searchString);
            return filteredEmployees;
        } catch (Exception e) {
            log.error("Error searching employees by name {}: {}", searchString, e.getMessage(), e);
            throw new EmployeeServiceException(EmployeeConstants.FAILED_TO_SEARCH_EMPLOYEES, e);
        }
    }

    public Employee getEmployeeByIdOrThrow(String id) {
        log.info("Getting employee by id: {}", id);
        Optional<Employee> employee = getEmployeeById(id);
        if (employee.isEmpty()) {
            throw new EmployeeNotFoundException(String.format(EmployeeConstants.EMPLOYEE_NOT_FOUND, id));
        }
        return employee.get();
    }

    public Integer getHighestSalary() {
        log.info("Getting highest salary of employees");
        try {
            List<Employee> employees = getAllEmployees();
            Optional<Integer> highestSalary = employees.stream()
                    .filter(employee -> employee.getEmployeeSalary() != null)
                    .map(Employee::getEmployeeSalary)
                    .max(Integer::compareTo);

            if (highestSalary.isEmpty()) {
                throw new EmployeeNotFoundException(EmployeeConstants.NO_EMPLOYEES_WITH_SALARY);
            }
            
            log.info("Highest salary found: {}", highestSalary.get());
            return highestSalary.get();
        } catch (EmployeeNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting highest salary: {}", e.getMessage(), e);
            throw new EmployeeServiceException(EmployeeConstants.FAILED_TO_GET_HIGHEST_SALARY, e);
        }
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        log.info("Getting top {} highest earning employee names", EmployeeConstants.TOP_EARNERS_LIMIT);
        try {
            List<Employee> employees = getAllEmployees();
            List<String> topTenNames = employees.stream()
                    .filter(employee -> employee.getEmployeeSalary() != null && employee.getEmployeeName() != null)
                    .sorted(Comparator.comparing(Employee::getEmployeeSalary).reversed())
                    .limit(EmployeeConstants.TOP_EARNERS_LIMIT)
                    .map(Employee::getEmployeeName)
                    .collect(Collectors.toList());

            log.info("Found {} top earning employees", topTenNames.size());
            return topTenNames;
        } catch (Exception e) {
            log.error("Error getting top {} highest earning employees: {}", EmployeeConstants.TOP_EARNERS_LIMIT, e.getMessage(), e);
            throw new EmployeeServiceException(EmployeeConstants.FAILED_TO_GET_TOP_EARNERS, e);
        }
    }


    public String deleteEmployeeById(String id) {
        log.info("Deleting employee by id: {}", id);
        
        Optional<Employee> employee = getEmployeeById(id);
        if (employee.isEmpty()) {
            throw new EmployeeNotFoundException(String.format(EmployeeConstants.EMPLOYEE_NOT_FOUND, id));
        }
        
        String employeeName = employee.get().getEmployeeName();
        boolean deleted = deleteEmployeeByName(employeeName);
        
        if (!deleted) {
            throw new EmployeeServiceException(EmployeeConstants.FAILED_TO_DELETE_EMPLOYEE + ": " + employeeName);
        }
        
        log.info("Successfully deleted employee: {}", employeeName);
        return employeeName;
    }

    // Private methods --->

    private Optional<Employee> getEmployeeById(String id) {
        validator.validateEmployeeId(id);
        return apiClient.getEmployeeById(id);
    }

    private boolean deleteEmployeeByName(String employeeName) {
        return apiClient.deleteEmployeeByName(employeeName);
    }

}
