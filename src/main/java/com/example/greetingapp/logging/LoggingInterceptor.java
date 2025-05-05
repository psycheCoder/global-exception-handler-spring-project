package com.example.greetingapp.logging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class LoggingInterceptor extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_LOGGING_KEY = "trackingId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(REQUEST_ID_LOGGING_KEY, requestId);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logRequest(wrappedRequest);
            logResponse(wrappedResponse);
            MDC.remove(REQUEST_ID_LOGGING_KEY);
            wrappedResponse.copyBodyToResponse(); // VERY IMPORTANT
        }
    }

    private void logRequest(ContentCachingRequestWrapper requestWrapper) {
        logger.info("----- Request -----");
        logger.info("URI         : {}", requestWrapper.getRequestURI());
        logger.info("Method      : {}", requestWrapper.getMethod());
        logger.info("Headers     :");
        Enumeration<String> headerNames = requestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = requestWrapper.getHeader(headerName);
            logger.info("  {} = {}", headerName, headerValue);
        }

        byte[] content = requestWrapper.getContentAsByteArray();
        if (content.length > 0) {
            String requestBody = new String(content, StandardCharsets.UTF_8);
            logger.info("Body        : {}", requestBody);
        }
        logger.info("-------------------");
    }

    private void logResponse(ContentCachingResponseWrapper responseWrapper) {
        logger.info("----- Response -----");
        logger.info("Status Code : {}", responseWrapper.getStatus());
        logger.info("Headers     :");
        for (String headerName : responseWrapper.getHeaderNames()) {
            String headerValue = responseWrapper.getHeader(headerName);
            logger.info("  {} = {}", headerName, headerValue);
        }

        byte[] content = responseWrapper.getContentAsByteArray();
        if (content.length > 0) {
            String responseBody = new String(content, StandardCharsets.UTF_8);
            logger.info("Body        : {}", responseBody);
        }
        logger.info("----------------------");
    }
}
