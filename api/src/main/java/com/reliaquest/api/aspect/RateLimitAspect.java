package com.reliaquest.api.aspect;

import com.reliaquest.api.annotation.RateLimit;
import com.reliaquest.api.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitConfig rateLimitConfig;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        
        // Get current HTTP request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // Not a web request, skip rate limiting
            return joinPoint.proceed();
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // Build rate limit key
        String rateLimitKey = buildRateLimitKey(request, rateLimit.key());
        
        // Get or create bucket
        Bucket bucket = rateLimitConfig.resolveBucket(rateLimitKey);
        
        // Try to consume token
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            log.debug("Rate limit check passed for key: {}. Remaining: {}", rateLimitKey, probe.getRemainingTokens());
            return joinPoint.proceed();
        } else {
            long waitTime = probe.getNanosToWaitForRefill() / 1_000_000_000;
            log.warn("Rate limit exceeded for key: {}. Retry after: {} seconds", rateLimitKey, waitTime);
            
            throw new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS, 
                rateLimit.message() + " Retry after " + waitTime + " seconds."
            );
        }
    }

    private String buildRateLimitKey(HttpServletRequest request, String keyExpression) {
        // Simple key resolution - can be enhanced with SpEL for complex expressions
        return switch (keyExpression) {
            case "default" -> "global";
            case "#request.remoteAddr" -> request.getRemoteAddr();
            case "ip" -> getClientIpAddress(request);
            default -> keyExpression; // Use as literal key
        };
    }

    private String getClientIpAddress(HttpServletRequest request) {
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
}
