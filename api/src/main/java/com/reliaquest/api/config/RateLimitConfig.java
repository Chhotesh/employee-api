package com.reliaquest.api.config;

import com.reliaquest.api.constants.EmployeeConstants;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    // In-memory cache for rate limit buckets (per IP/user)
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    public Bucket createNewBucket(int capacity, int tokens) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(tokens, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Get or create bucket for the given key
     */
    public Bucket resolveBucket(String key) {
        return bucketCache.computeIfAbsent(key, k -> createBucketForEndpoint(key));
    }

    /**
     * Different rate limits for different endpoints
     */
    public Bucket createBucketForEndpoint(String key) {
        String endpoint = "";
        if (!StringUtils.isEmpty(key) && key.contains(":")) {
            endpoint = key.substring(key.lastIndexOf(":") + 1);
        }
        return switch (endpoint) {
            case EmployeeConstants.GET_ALL_EMPLOYEES_ENDPOINT -> createNewBucket(50,50);
            case EmployeeConstants.CREATE_EMPLOYEE_ENDPOINT, EmployeeConstants.DELETE_EMPLOYEE_ENDPOINT -> createNewBucket(10,10);
            case EmployeeConstants.GET_EMPLOYEE_BY_ID_ENDPOINT, EmployeeConstants.SEARCH_EMPLOYEES_ENDPOINT -> createNewBucket(50,50);
            default -> createNewBucket(100,100); // Default rate limit
        };
    }
}
