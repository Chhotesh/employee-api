package com.reliaquest.api.validator;

import com.reliaquest.api.constants.EmployeeConstants;
import com.reliaquest.api.exception.EmployeeValidationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EmployeeValidator {

    public void validateSearchString(String searchString) {
        if (searchString == null || searchString.trim().isEmpty()) {
            throw new EmployeeValidationException(EmployeeConstants.SEARCH_STRING_NULL_OR_EMPTY);
        }
    }

    public void validateEmployeeId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new EmployeeValidationException(EmployeeConstants.EMPLOYEE_ID_NULL_OR_EMPTY);
        }
        
        // Validate UUID format
        try {
            UUID.fromString(id.trim());
        } catch (IllegalArgumentException e) {
            throw new EmployeeValidationException(EmployeeConstants.EMPLOYEE_ID_INVALID_UUID);
        }
    }

}
