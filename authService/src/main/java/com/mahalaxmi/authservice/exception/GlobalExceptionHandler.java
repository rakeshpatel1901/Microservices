package com.mahalaxmi.authservice.exception;


import com.mahalaxmi.authservice.dto.ApiResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PhoneNotVerifiedException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handlePhoneNotVerifiedException(
            PhoneNotVerifiedException e){
        ApiResponseDTO<Void> apiResponseDTO = new ApiResponseDTO<>();
        apiResponseDTO.setStatusCode("400");
        apiResponseDTO.setMessage(e.getMessage());
        apiResponseDTO.setStatus("Failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDTO);

    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleInvalidOtpException(InvalidOtpException e){
        ApiResponseDTO<Void> apiResponseDTO = new ApiResponseDTO<>();
        apiResponseDTO.setStatusCode("400");
        apiResponseDTO.setMessage(e.getMessage());
        apiResponseDTO.setStatus("Failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDTO);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExists(EmailAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(UserDoesNotExistsException.class)
    public ResponseEntity<String> handleUserDoesNotExists(UserDoesNotExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    @ExceptionHandler(PhoneAlreadyExistsException.class)
    public ResponseEntity<String> handlePhoneAlreadyExists(PhoneAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e){
        return ResponseEntity.status(401).body(e.getMessage());
    }
    @ExceptionHandler(EmailDoesNotExistsException.class)
    public ResponseEntity<String> handleEmailDoesNotExists(EmailDoesNotExistsException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleIllegalArgumentException(IllegalArgumentException e){
        ApiResponseDTO<String> apiResponseDTO = new ApiResponseDTO<>();
        apiResponseDTO.setMessage(e.getMessage());
        apiResponseDTO.setStatus("FAILED");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponseDTO);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException (MissingServletRequestParameterException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> errorMap = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMap.put(fieldName, message);
        });
        return errorMap;
    }

}
