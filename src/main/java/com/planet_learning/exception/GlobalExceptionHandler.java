package com.planet_learning.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.planet_learning.api_response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler 
{
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // Handle Runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Object> handleRuntimeException(RuntimeException ex) {
    	logger.error("An error occurred {}", List.of(ex.getMessage()));
        return ApiResponse.error("An error occurred", List.of(ex.getMessage()));
    }
    
    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleGeneralException(Exception ex) 
    {
    	logger.error("An error occurred {}", List.of(ex.getMessage()));
        return ApiResponse.error("An error occurred", List.of(ex.getMessage()));
    }
}