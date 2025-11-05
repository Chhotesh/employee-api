package com.reliaquest.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * Number of requests allowed per time window
     */
    int requests() default 100;
    
    /**
     * Time window in seconds
     */
    int windowSeconds() default 60;
    
    /**
     * Rate limit key - can use SpEL expressions
     * Examples: 
     * - "default" (global rate limit)
     * - "#request.remoteAddr" (per IP)
     * - "#request.getHeader('X-User-ID')" (per user)
     */
    String key() default "default";
    
    /**
     * Custom error message when rate limit is exceeded
     */
    String message() default "Rate limit exceeded. Please try again later.";
}
