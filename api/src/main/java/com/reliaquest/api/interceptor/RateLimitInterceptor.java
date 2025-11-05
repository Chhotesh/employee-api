package com.reliaquest.api.interceptor;

import com.reliaquest.api.config.RateLimitConfig;
import com.reliaquest.api.constants.EmployeeConstants;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitConfig rateLimitConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // Get client identifier (IP address or user ID)
        String clientId = getClientId(request);
        
        // Get endpoint identifier for different rate limits
        String endpoint = getEndpointIdentifier(request);
        
        // Create bucket key combining client and endpoint
        String bucketKey = clientId + ":" + endpoint;
        
        // Get or create bucket for this client-endpoint combination
        Bucket bucket = rateLimitConfig.resolveBucket(bucketKey);
        
        // Try to consume 1 token
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            // Request allowed - add rate limit headers
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", "60");
            log.debug("Request allowed for client: {} on endpoint: {}. Remaining tokens: {}", 
                     clientId, endpoint, probe.getRemainingTokens());
            return true;
        } else {
            // Rate limit exceeded
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.addHeader("Content-Type", "application/json");
            
            String errorResponse = String.format(
                "{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests. Try again in %d seconds.\",\"status\":429}",
                waitForRefill
            );
            response.getWriter().write(errorResponse);
            
            log.warn("Rate limit exceeded for client: {} on endpoint: {}. Retry after: {} seconds", 
                    clientId, endpoint, waitForRefill);
            return false;
        }
    }

    /**
     * Get client identifier - can be IP, user ID, API key, etc.
     */
    private String getClientId(HttpServletRequest request) {
        // Try to get real IP address
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Map request to endpoint identifier for different rate limits
     */
    private String getEndpointIdentifier(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        
        return switch (method + " " + path) {
            case "GET /api/v1/employee" -> EmployeeConstants.GET_ALL_EMPLOYEES_ENDPOINT;
            case "POST /api/v1/employee" -> EmployeeConstants.CREATE_EMPLOYEE_ENDPOINT;
            default -> {
                if (path.matches("/api/v1/employee/[^/]+") && "GET".equals(method)) {
                    yield EmployeeConstants.GET_EMPLOYEE_BY_ID_ENDPOINT;
                } else if (path.matches("/api/v1/employee/[^/]+") && "DELETE".equals(method)) {
                    yield EmployeeConstants.DELETE_EMPLOYEE_ENDPOINT;
                } else if (path.contains("/search")) {
                    yield EmployeeConstants.SEARCH_EMPLOYEES_ENDPOINT;
                } else {
                    yield "DEFAULT";
                }
            }
        };
    }
}
