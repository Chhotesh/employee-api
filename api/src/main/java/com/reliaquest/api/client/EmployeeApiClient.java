package com.reliaquest.api.client;

import com.reliaquest.api.constants.EmployeeConstants;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.DeleteEmployeeRequest;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeApiClient {

    private final RestTemplate restTemplate;
    
    @Value("${employee.api.base-url:http://localhost:8112/api/v1/employee}")
    private String baseUrl;

    @Retryable(
            value = {HttpServerErrorException.class, EmployeeServiceException.class},
            exclude = {HttpClientErrorException.TooManyRequests.class, HttpClientErrorException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2, maxDelay = 10000)
    )
    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees from mock API");
        try {
            ResponseEntity<ApiResponse<List<Employee>>> response = restTemplate.exchange(
                    baseUrl, 
                    HttpMethod.GET, 
                    null, 
                    new ParameterizedTypeReference<ApiResponse<List<Employee>>>() {}
            );

            return extractDataFromResponse(response, EmployeeConstants.FAILED_TO_FETCH_EMPLOYEES);
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("Rate limit exceeded when fetching all employees. Status: {}, Headers: {}", 
                    e.getStatusCode(), e.getResponseHeaders());
            throw new EmployeeServiceException(EmployeeConstants.RATE_LIMIT_EXCEEDED, e);
        } catch (Exception e) {
            log.error("Error fetching all employees: {}", e.getMessage(), e);
            throw new EmployeeServiceException(EmployeeConstants.FAILED_TO_FETCH_EMPLOYEES, e);
        }
    }

    @Retryable(
            value = {HttpServerErrorException.class, EmployeeServiceException.class},
            exclude = {HttpClientErrorException.TooManyRequests.class, HttpClientErrorException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2, maxDelay = 10000)
    )
    public Optional<Employee> getEmployeeById(String id) {
        log.info("Fetching employee with id: {}", id);
        try {
            ResponseEntity<ApiResponse<Employee>> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<Employee>>() {}
            );

            Employee employee = extractDataFromResponse(response, EmployeeConstants.FAILED_TO_FETCH_EMPLOYEE);
            if (employee != null) {
                log.info("Successfully fetched employee: {}", employee.getEmployeeName());
                return Optional.of(employee);
            }
            return Optional.empty();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Employee with id {} not found", id);
            return Optional.empty();
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("Rate limit exceeded when fetching employee {}. Status: {}, Headers: {}", 
                    id, e.getStatusCode(), e.getResponseHeaders());
            throw new EmployeeServiceException(EmployeeConstants.RATE_LIMIT_EXCEEDED, e);
        } catch (Exception e) {
            log.error("Error fetching employee with id {}: {}", id, e.getMessage(), e);
            throw new EmployeeServiceException(EmployeeConstants.FAILED_TO_FETCH_EMPLOYEE, e);
        }
    }

    @Retryable(
            value = {HttpServerErrorException.class, EmployeeServiceException.class},
            exclude = {HttpClientErrorException.TooManyRequests.class, HttpClientErrorException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2, maxDelay = 10000)
    )
    public Employee createEmployee(EmployeeInput employeeInput) {
        log.info("Creating employee: {}", employeeInput.getName());
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EmployeeInput> request = new HttpEntity<>(employeeInput, headers);

            ResponseEntity<ApiResponse<Employee>> response = restTemplate.exchange(
                    baseUrl, 
                    HttpMethod.POST, 
                    request, 
                    new ParameterizedTypeReference<ApiResponse<Employee>>() {}
            );

            Employee createdEmployee = extractDataFromResponse(response, EmployeeConstants.FAILED_TO_CREATE_EMPLOYEE);
            log.info("Successfully created employee: {}", createdEmployee.getEmployeeName());
            return createdEmployee;
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("Rate limit exceeded when creating employee {}. Status: {}, Headers: {}", 
                    employeeInput.getName(), e.getStatusCode(), e.getResponseHeaders());
            throw new EmployeeServiceException(EmployeeConstants.RATE_LIMIT_EXCEEDED, e);
        } catch (Exception e) {
            log.error("Error creating employee {}: {}", employeeInput.getName(), e.getMessage(), e);
            throw new EmployeeServiceException(EmployeeConstants.FAILED_TO_CREATE_EMPLOYEE, e);
        }
    }

    @Retryable(
            value = {HttpServerErrorException.class, EmployeeServiceException.class},
            exclude = {HttpClientErrorException.TooManyRequests.class, HttpClientErrorException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2, maxDelay = 10000)
    )
    public boolean deleteEmployeeByName(String employeeName) {
        log.info("Deleting employee: {}", employeeName);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            DeleteEmployeeRequest deleteRequest = new DeleteEmployeeRequest(employeeName);
            HttpEntity<DeleteEmployeeRequest> request = new HttpEntity<>(deleteRequest, headers);
            
            ResponseEntity<ApiResponse<Boolean>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.DELETE,
                    request,
                    new ParameterizedTypeReference<ApiResponse<Boolean>>() {}
            );

            Boolean result = extractDataFromResponse(response, EmployeeConstants.FAILED_TO_DELETE_EMPLOYEE);
            boolean deleted = Boolean.TRUE.equals(result);
            if (deleted) {
                log.info("Successfully deleted employee: {}", employeeName);
            }
            return deleted;
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("Rate limit exceeded when deleting employee {}. Status: {}, Headers: {}", 
                    employeeName, e.getStatusCode(), e.getResponseHeaders());
            throw new EmployeeServiceException(EmployeeConstants.RATE_LIMIT_EXCEEDED, e);
        } catch (Exception e) {
            log.error("Error deleting employee {}: {}", employeeName, e.getMessage(), e);
            throw new EmployeeServiceException(EmployeeConstants.FAILED_TO_DELETE_EMPLOYEE, e);
        }
    }

    // private methods ---->

    private <T> T extractDataFromResponse(ResponseEntity<ApiResponse<T>> response, String errorMessage) {
        ApiResponse<T> apiResponse = response.getBody();
        if (apiResponse != null && apiResponse.getData() != null) {
            return apiResponse.getData();
        }
        
        if (apiResponse != null && apiResponse.getData() == null) {
            log.warn("Received null data from API response");
            return null;
        }
        
        log.warn("Received null response from API");
        throw new EmployeeServiceException(errorMessage + " - null response");
    }
}
