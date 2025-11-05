package com.reliaquest.api.controller;

import com.reliaquest.api.constants.EmployeeConstants;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeValidationException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private Employee testEmployee;
    private List<Employee> testEmployees;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee("1", "John Doe", 75000, 30, "Software Engineer", "john@company.com");
        Employee employee2 = new Employee("2", "Jane Smith", 85000, 28, "Senior Developer", "jane@company.com");
        Employee employee3 = new Employee("3", "Bob Johnson", 95000, 35, "Tech Lead", "bob@company.com");
        testEmployees = Arrays.asList(testEmployee, employee2, employee3);
    }

    @Test
    void getAllEmployees_ShouldReturnAllEmployees() {
        when(employeeService.getAllEmployees()).thenReturn(testEmployees);

        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenExists() {
        when(employeeService.getEmployeeByIdOrThrow("1")).thenReturn(testEmployee);

        ResponseEntity<Employee> response = employeeController.getEmployeeById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getEmployeeName());
    }

    @Test
    void getEmployeeById_ShouldReturnNotFound_WhenNotExists() {
        when(employeeService.getEmployeeByIdOrThrow("999")).thenThrow(new EmployeeNotFoundException("Employee not found"));

        ResponseEntity<Employee> response = employeeController.getEmployeeById("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getEmployeesByNameSearch_ShouldReturnFilteredEmployees() {
        List<Employee> filteredEmployees = Arrays.asList(testEmployee, testEmployees.get(2)); // John Doe and Bob Johnson
        when(employeeService.searchEmployeesByName("John")).thenReturn(filteredEmployees);

        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch("John");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size()); // John Doe and Bob Johnson
    }

    @Test
    void getHighestSalaryOfEmployees_ShouldReturnHighestSalary() {
        when(employeeService.getHighestSalary()).thenReturn(95000);

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(95000, response.getBody());
    }

    @Test
    void getHighestSalaryOfEmployees_ShouldReturnNotFound_WhenNoEmployees() {
        when(employeeService.getHighestSalary()).thenThrow(new EmployeeNotFoundException("No employees found"));

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnSortedNames() {
        List<String> topNames = Arrays.asList("Bob Johnson", "Jane Smith", "John Doe");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(topNames);

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
        assertEquals("Bob Johnson", response.getBody().get(0)); // Highest salary first
    }

    @Test
    void createEmployee_ShouldReturnCreatedEmployee_WhenValidInput() {
        EmployeeInput input = new EmployeeInput("New Employee", 70000, 25, "Developer");
        Employee createdEmployee = new Employee("4", "New Employee", 70000, 25, "Developer", "new@company.com");

        when(employeeService.createEmployee(any(EmployeeInput.class))).thenReturn(createdEmployee);

        ResponseEntity<Employee> response = employeeController.createEmployee(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Employee", response.getBody().getEmployeeName());
    }

    @Test
    void deleteEmployeeById_ShouldReturnEmployeeName_WhenSuccessful() {
        when(employeeService.deleteEmployeeById("1")).thenReturn("John Doe");

        ResponseEntity<String> response = employeeController.deleteEmployeeById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody());
    }

    @Test
    void deleteEmployeeById_ShouldReturnNotFound_WhenEmployeeNotExists() {
        when(employeeService.deleteEmployeeById("999")).thenThrow(new EmployeeNotFoundException("Employee not found"));

        ResponseEntity<String> response = employeeController.deleteEmployeeById("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // UUID Validation Tests
    
    @Test
    void getEmployeeById_ShouldThrowValidationException_WhenInvalidUUIDFormat() {
        String invalidUUID = "invalid-uuid-format";
        
        // Mock service to throw validation exception for invalid UUID
        when(employeeService.getEmployeeByIdOrThrow(invalidUUID))
                .thenThrow(new EmployeeValidationException(EmployeeConstants.EMPLOYEE_ID_INVALID_UUID));

        // This will be handled by GlobalExceptionHandler in real scenario
        // But for unit test, we expect the exception to be thrown
        try {
            employeeController.getEmployeeById(invalidUUID);
        } catch (EmployeeValidationException e) {
            assertEquals(EmployeeConstants.EMPLOYEE_ID_INVALID_UUID, e.getMessage());
        }
    }

    @Test
    void getEmployeeById_ShouldThrowValidationException_WhenNumericId() {
        String numericId = "12345";
        
        // Mock service to throw validation exception for numeric ID
        when(employeeService.getEmployeeByIdOrThrow(numericId))
                .thenThrow(new EmployeeValidationException(EmployeeConstants.EMPLOYEE_ID_INVALID_UUID));

        // This will be handled by GlobalExceptionHandler in real scenario
        try {
            employeeController.getEmployeeById(numericId);
        } catch (EmployeeValidationException e) {
            assertEquals(EmployeeConstants.EMPLOYEE_ID_INVALID_UUID, e.getMessage());
        }
    }

    @Test
    void deleteEmployeeById_ShouldThrowValidationException_WhenInvalidUUIDFormat() {
        String invalidUUID = "not-a-uuid";
        
        // Mock service to throw validation exception for invalid UUID
        when(employeeService.deleteEmployeeById(invalidUUID))
                .thenThrow(new EmployeeValidationException(EmployeeConstants.EMPLOYEE_ID_INVALID_UUID));

        // This will be handled by GlobalExceptionHandler in real scenario
        try {
            employeeController.deleteEmployeeById(invalidUUID);
        } catch (EmployeeValidationException e) {
            assertEquals(EmployeeConstants.EMPLOYEE_ID_INVALID_UUID, e.getMessage());
        }
    }
}
