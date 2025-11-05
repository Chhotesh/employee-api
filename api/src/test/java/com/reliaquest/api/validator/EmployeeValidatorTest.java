package com.reliaquest.api.validator;

import com.reliaquest.api.constants.EmployeeConstants;
import com.reliaquest.api.exception.EmployeeValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeValidatorTest {

    private EmployeeValidator employeeValidator;

    @BeforeEach
    void setUp() {
        employeeValidator = new EmployeeValidator();
    }

    @Test
    void validateEmployeeId_ShouldPass_WhenValidUUID() {
        String validUUID = "550e8400-e29b-41d4-a716-446655440000";
        
        assertDoesNotThrow(() -> employeeValidator.validateEmployeeId(validUUID));
    }

    @Test
    void validateEmployeeId_ShouldThrowException_WhenNullId() {
        EmployeeValidationException exception = assertThrows(
            EmployeeValidationException.class,
            () -> employeeValidator.validateEmployeeId(null)
        );
        
        assertEquals(EmployeeConstants.EMPLOYEE_ID_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void validateEmployeeId_ShouldThrowException_WhenEmptyId() {
        EmployeeValidationException exception = assertThrows(
            EmployeeValidationException.class,
            () -> employeeValidator.validateEmployeeId("")
        );
        
        assertEquals(EmployeeConstants.EMPLOYEE_ID_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void validateEmployeeId_ShouldThrowException_WhenInvalidUUIDFormat() {
        String invalidUUID = "invalid-uuid-format";
        
        EmployeeValidationException exception = assertThrows(
            EmployeeValidationException.class,
            () -> employeeValidator.validateEmployeeId(invalidUUID)
        );
        
        assertEquals(EmployeeConstants.EMPLOYEE_ID_INVALID_UUID, exception.getMessage());
    }

    @Test
    void validateEmployeeId_ShouldThrowException_WhenNumericId() {
        String numericId = "12345";
        
        EmployeeValidationException exception = assertThrows(
            EmployeeValidationException.class,
            () -> employeeValidator.validateEmployeeId(numericId)
        );
        
        assertEquals(EmployeeConstants.EMPLOYEE_ID_INVALID_UUID, exception.getMessage());
    }

    @Test
    void validateEmployeeId_ShouldPass_WhenValidUUIDWithWhitespace() {
        String validUUIDWithSpaces = "  550e8400-e29b-41d4-a716-446655440000  ";
        
        assertDoesNotThrow(() -> employeeValidator.validateEmployeeId(validUUIDWithSpaces));
    }

    @Test
    void validateSearchString_ShouldPass_WhenValidString() {
        String validSearchString = "John";
        
        assertDoesNotThrow(() -> employeeValidator.validateSearchString(validSearchString));
    }

    @Test
    void validateSearchString_ShouldThrowException_WhenNullString() {
        EmployeeValidationException exception = assertThrows(
            EmployeeValidationException.class,
            () -> employeeValidator.validateSearchString(null)
        );
        
        assertEquals(EmployeeConstants.SEARCH_STRING_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void validateSearchString_ShouldThrowException_WhenEmptyString() {
        EmployeeValidationException exception = assertThrows(
            EmployeeValidationException.class,
            () -> employeeValidator.validateSearchString("   ")
        );
        
        assertEquals(EmployeeConstants.SEARCH_STRING_NULL_OR_EMPTY, exception.getMessage());
    }
}
