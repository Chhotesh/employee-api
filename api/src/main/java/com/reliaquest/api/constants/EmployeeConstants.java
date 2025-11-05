package com.reliaquest.api.constants;

public final class EmployeeConstants {
    
    // Validation constants
    public static final int MIN_AGE = 16;
    public static final int MAX_AGE = 75;
    public static final int MIN_SALARY = 1;
    public static final int TOP_EARNERS_LIMIT = 10;
    
    // Error messages
    public static final String EMPLOYEE_NAME_REQUIRED = "Employee name is required and cannot be empty";
    public static final String EMPLOYEE_SALARY_REQUIRED = "Employee salary is required";
    public static final String EMPLOYEE_SALARY_INVALID = "Employee salary must be greater than 0";
    public static final String EMPLOYEE_AGE_REQUIRED = "Employee age is required";
    public static final String EMPLOYEE_AGE_MIN_INVALID = "Employee age must be at least 16";
    public static final String EMPLOYEE_AGE_MAX_INVALID = "Employee age must be at most 75";
    public static final String EMPLOYEE_AGE_INVALID = "Employee age must be between 16 and 75";
    public static final String EMPLOYEE_TITLE_REQUIRED = "Employee title is required and cannot be empty";
    public static final String EMPLOYEE_NOT_FOUND = "Employee with id %s not found";
    public static final String NO_EMPLOYEES_WITH_SALARY = "No employees found with salary information";
    public static final String EMPLOYEE_ID_INVALID_UUID = "Employee ID must be a valid UUID format. Please provide a valid UUID.";
    public static final String EMPLOYEE_ID_NULL_OR_EMPTY = "Employee ID cannot be null or empty";
    public static final String SEARCH_STRING_NULL_OR_EMPTY = "Search string cannot be null or empty";
    
    // API operation messages
    public static final String FAILED_TO_FETCH_EMPLOYEES = "Failed to fetch employees";
    public static final String FAILED_TO_FETCH_EMPLOYEE = "Failed to fetch employee";
    public static final String FAILED_TO_CREATE_EMPLOYEE = "Failed to create employee";
    public static final String FAILED_TO_DELETE_EMPLOYEE = "Failed to delete employee";
    public static final String FAILED_TO_SEARCH_EMPLOYEES = "Failed to search employees";
    public static final String FAILED_TO_GET_HIGHEST_SALARY = "Failed to get highest salary";
    public static final String FAILED_TO_GET_TOP_EARNERS = "Failed to get top earning employees";
    public static final String RATE_LIMIT_EXCEEDED = "Rate limit exceeded. Please try again later.";
    
    private EmployeeConstants() {
    }
}
