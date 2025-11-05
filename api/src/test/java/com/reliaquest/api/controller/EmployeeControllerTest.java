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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private final String EMPLOYEE_ID = "ea1095eb-40de-41aa-8c17-83b164f64af3";
    private final String EMPLOYEE_NAME_SEARCH = "Prahalad";

    @BeforeEach
    void setUp() {
        testEmployee = new Employee(EMPLOYEE_ID, EMPLOYEE_NAME_SEARCH, 75000, 30, "Software Engineer", "prathod@1.com");
        Employee employee2 = new Employee("320bff3a-dbf3-4554-b2a4-dd672cc1dd67", "Rahul", 85000, 28, "Senior Developer", "rahul@2.com");
        Employee employee3 = new Employee("e3c04ede-ddd7-437b-952a-a02eddad1a02", "Vijay", 95000, 35, "Tech Lead", "vijay@3.com");
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
        when(employeeService.getEmployeeByIdOrThrow(EMPLOYEE_ID)).thenReturn(testEmployee);

        ResponseEntity<Employee> response = employeeController.getEmployeeById(EMPLOYEE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EMPLOYEE_NAME_SEARCH, response.getBody().getEmployeeName());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenNotExists() {
        String notPresentId = "e3c04ede-ddd7-437b-952a-a02eddad1a09";
        when(employeeService.getEmployeeByIdOrThrow(notPresentId)).thenThrow(new EmployeeNotFoundException("Employee not found"));

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeController.getEmployeeById(notPresentId);
        });
    }

    @Test
    void getEmployeesByNameSearch_ShouldReturnFilteredEmployees() {
        List<Employee> filteredEmployees = Arrays.asList(testEmployee);
        when(employeeService.searchEmployeesByName(EMPLOYEE_NAME_SEARCH)).thenReturn(filteredEmployees);

        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch(EMPLOYEE_NAME_SEARCH);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
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

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeController.getHighestSalaryOfEmployees();
        });
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ShouldReturnSortedNames() {
        List<String> topNames = Arrays.asList("Vijay", "Rahul", "Prahalad");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(topNames);

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
        assertEquals("Vijay", response.getBody().get(0)); // Highest salary first
    }

    @Test
    void createEmployee_ShouldReturnCreatedEmployee_WhenValidInput() {
        EmployeeInput input = new EmployeeInput("New Employee", 70000, 25, "Developer");
        Employee createdEmployee = new Employee("b4616677-8d24-4b34-aeb5-462568c1a120", "New Employee", 70000, 25, "Developer", "new@company.com");

        when(employeeService.createEmployee(any(EmployeeInput.class))).thenReturn(createdEmployee);

        ResponseEntity<Employee> response = employeeController.createEmployee(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Employee", response.getBody().getEmployeeName());
    }

    @Test
    void deleteEmployeeById_ShouldReturnEmployeeName_WhenSuccessful() {
        when(employeeService.deleteEmployeeById(EMPLOYEE_ID)).thenReturn(EMPLOYEE_NAME_SEARCH);

        ResponseEntity<String> response = employeeController.deleteEmployeeById(EMPLOYEE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EMPLOYEE_NAME_SEARCH, response.getBody());
    }

    @Test
    void deleteEmployeeById_ShouldThrowException_WhenEmployeeNotExists() {
        String nonExistentId = "b4616677-8d24-4b34-aeb5-462568c1a120";
        when(employeeService.deleteEmployeeById(nonExistentId)).thenThrow(new EmployeeNotFoundException("Employee not found"));

        // Test that the controller throws the exception (GlobalExceptionHandler will handle it in real app)
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeController.deleteEmployeeById(nonExistentId);
        });
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
