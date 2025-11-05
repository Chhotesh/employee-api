package com.reliaquest.api.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    // In-memory cache for rate limit buckets (per IP/user)
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    /**
     * Creates a rate limit bucket for the given key (IP address, user ID, etc.)
     * Rate limit: 100 requests per minute per key
     */
    public Bucket createNewBucket() {
        // Allow 100 requests per minute with refill of 100 tokens every minute
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Get or create bucket for the given key
     */
    public Bucket resolveBucket(String key) {
        return bucketCache.computeIfAbsent(key, k -> createNewBucket());
    }

    /**
     * Different rate limits for different endpoints
     */
    public Bucket createBucketForEndpoint(String endpoint) {
        return switch (endpoint) {
            case "GET_ALL_EMPLOYEES" -> {
                // More restrictive for expensive operations
                Bandwidth limit = Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(1)));
                yield Bucket.builder().addLimit(limit).build();
            }
            case "CREATE_EMPLOYEE", "DELETE_EMPLOYEE" -> {
                // Very restrictive for write operations
                Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
                yield Bucket.builder().addLimit(limit).build();
            }
            case "GET_EMPLOYEE_BY_ID", "SEARCH_EMPLOYEES" -> {
                // Moderate limits for read operations
                Bandwidth limit = Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1)));
                yield Bucket.builder().addLimit(limit).build();
            }
            default -> createNewBucket(); // Default rate limit
        };
    }
}
