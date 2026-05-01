package com.ralph.marketdatafeed.filters;

import com.ralph.marketdatafeed.enums.UserTier;
import com.ralph.marketdatafeed.service.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitingFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    private final RateLimiter rateLimiter;

    public RateLimitingFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String userId = extractUserId(request);
        UserTier tier = extractTier(request);

        if (userId == null || rateLimiter.isAllowed(userId, tier)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"Rate limit exceeded. Try again shortly.\"}");
        }
    }

    private String extractUserId(HttpServletRequest request) {
        return request.getHeader("userId");
    }

    private UserTier extractTier(HttpServletRequest request) {
        String tier = request.getHeader("userTier");
        logger.info("Tier =>{}",tier);
        return "PRO".equalsIgnoreCase(tier)
                ? UserTier.PRO
                : UserTier.FREE;
    }
}
