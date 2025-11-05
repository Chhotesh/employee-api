package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reliaquest.api.constants.EmployeeConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInput {

    @JsonProperty("name")
    @NotBlank(message = EmployeeConstants.EMPLOYEE_NAME_REQUIRED)
    private String name;

    @JsonProperty("salary")
    @NotNull(message = EmployeeConstants.EMPLOYEE_SALARY_REQUIRED)
    @Min(value = EmployeeConstants.MIN_SALARY, message = EmployeeConstants.EMPLOYEE_SALARY_INVALID)
    private Integer salary;

    @JsonProperty("age")
    @NotNull(message = EmployeeConstants.EMPLOYEE_AGE_REQUIRED)
    @Min(value = EmployeeConstants.MIN_AGE, message = EmployeeConstants.EMPLOYEE_AGE_MIN_INVALID)
    @Max(value = EmployeeConstants.MAX_AGE, message = EmployeeConstants.EMPLOYEE_AGE_MAX_INVALID)
    private Integer age;

    @JsonProperty("title")
    @NotBlank(message = EmployeeConstants.EMPLOYEE_TITLE_REQUIRED)
    private String title;
}
