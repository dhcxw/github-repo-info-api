package com.example.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理仓库不存在异常
     * 
     * @param ex 仓库不存在异常
     * @return HTTP 404响应
     */
    @ExceptionHandler(RepositoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRepositoryNotFound(RepositoryNotFoundException ex) {
        logger.error("Repository not found: {}", ex.getMessage());
        
        // 构建统一格式的错误响应体
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * 处理GitHub API调用异常
     * 
     * @param ex GitHub API异常
     * @return HTTP 502响应
     */
    @ExceptionHandler(GithubApiException.class)
    public ResponseEntity<Map<String, Object>> handleGithubApiException(GithubApiException ex) {
        logger.error("GitHub API error: {}", ex.getMessage());
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_GATEWAY.value());
        body.put("error", "Bad Gateway");
        body.put("message", "Error communicating with GitHub API: " + ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.BAD_GATEWAY);
    }

    /**
     * 处理通用异常
     * 
     * @param ex 任意异常
     * @return HTTP 500响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred");
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
