package com.reliaquest.api.model;

import com.reliaquest.api.constants.EmployeeConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeInputValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validateEmployeeInput_ShouldPass_WhenAllFieldsValid() {
        EmployeeInput input = new EmployeeInput("John Doe", 50000, 30, "Software Engineer");
        
        Set<ConstraintViolation<EmployeeInput>> violations = validator.validate(input);
        
        assertTrue(violations.isEmpty());
    }

    @Test
    void validateEmployeeInput_ShouldFail_WhenNameIsBlank() {
        EmployeeInput input = new EmployeeInput("", 50000, 30, "Software Engineer");
        
        Set<ConstraintViolation<EmployeeInput>> violations = validator.validate(input);
        
        assertEquals(1, violations.size());
        ConstraintViolation<EmployeeInput> violation = violations.iterator().next();
        assertEquals(EmployeeConstants.EMPLOYEE_NAME_REQUIRED, violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void validateEmployeeInput_ShouldFail_WhenSalaryIsNull() {
        EmployeeInput input = new EmployeeInput("John Doe", null, 30, "Software Engineer");
        
        Set<ConstraintViolation<EmployeeInput>> violations = validator.validate(input);
        
        assertEquals(1, violations.size());
        ConstraintViolation<EmployeeInput> violation = violations.iterator().next();
        assertEquals(EmployeeConstants.EMPLOYEE_SALARY_REQUIRED, violation.getMessage());
        assertEquals("salary", violation.getPropertyPath().toString());
    }

    @Test
    void validateEmployeeInput_ShouldFail_WhenSalaryIsTooLow() {
        EmployeeInput input = new EmployeeInput("John Doe", 0, 30, "Software Engineer");
        
        Set<ConstraintViolation<EmployeeInput>> violations = validator.validate(input);
        
        assertEquals(1, violations.size());
        ConstraintViolation<EmployeeInput> violation = violations.iterator().next();
        assertEquals(EmployeeConstants.EMPLOYEE_SALARY_INVALID, violation.getMessage());
        assertEquals("salary", violation.getPropertyPath().toString());
    }

    @Test
    void validateEmployeeInput_ShouldFail_WhenAgeIsNull() {
        EmployeeInput input = new EmployeeInput("John Doe", 50000, null, "Software Engineer");
        
        Set<ConstraintViolation<EmployeeInput>> violations = validator.validate(input);
        
        assertEquals(1, violations.size());
        ConstraintViolation<EmployeeInput> violation = violations.iterator().next();
        assertEquals(EmployeeConstants.EMPLOYEE_AGE_REQUIRED, violation.getMessage());
        assertEquals("age", violation.getPropertyPath().toString());
    }

    @Test
    void validateEmployeeInput_ShouldFail_WhenAgeTooYoung() {
        EmployeeInput input = new EmployeeInput("John Doe", 50000, 15, "Software Engineer");
        
        Set<ConstraintViolation<EmployeeInput>> violations = validator.validate(input);
        
        assertEquals(1, violations.size());
        ConstraintViolation<EmployeeInput> violation = violations.iterator().next();
        assertEquals(EmployeeConstants.EMPLOYEE_AGE_MIN_INVALID, violation.getMessage());
        assertEquals("age", violation.getPropertyPath().toString());
    }

    @Test
    void validateEmployeeInput_ShouldFail_WhenAgeTooOld() {
        EmployeeInput input = new EmployeeInput("John Doe", 50000, 76, "Software Engineer");
        
        Set<ConstraintViolation<EmployeeInput>> violations = validator.validate(input);
        
        assertEquals(1, violations.size());
        ConstraintViolation<EmployeeInput> violation = violations.iterator().next();
        assertEquals(EmployeeConstants.EMPLOYEE_AGE_MAX_INVALID, violation.getMessage());
        assertEquals("age", violation.getPropertyPath().toString());
    }

    @Test
    void validateEmployeeInput_ShouldFail_WhenTitleIsBlank() {
        EmployeeInput input = new EmployeeInput("John Doe", 50000, 30, "");
        
        Set<ConstraintViolation<EmployeeInput>> violations = validator.validate(input);
        
        assertEquals(1, violations.size());
        ConstraintViolation<EmployeeInput> violation = violations.iterator().next();
        assertEquals(EmployeeConstants.EMPLOYEE_TITLE_REQUIRED, violation.getMessage());
        assertEquals("title", violation.getPropertyPath().toString());
    }

    @Test
    void validateEmployeeInput_ShouldFail_WithMultipleViolations() {
        EmployeeInput input = new EmployeeInput("", -1000, 10, "");
        
        Set<ConstraintViolation<EmployeeInput>> violations = validator.validate(input);
        
        assertEquals(4, violations.size()); // name, salary, age, title violations
    }
}
