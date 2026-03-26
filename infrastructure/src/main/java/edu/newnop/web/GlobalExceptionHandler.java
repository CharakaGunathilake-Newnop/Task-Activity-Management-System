package edu.newnop.web;

import edu.newnop.common.ApiError;
import edu.newnop.infrastructure.adapters.in.web.exceptions.EmailNotVerifiedException;
import edu.newnop.infrastructure.adapters.in.web.exceptions.InvalidUserRoleException;
import edu.newnop.infrastructure.adapters.in.web.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles @Valid failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.error(
                        "Bad Request",
                        null,
                        errors
                ));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError<Void>> handleUserAlreadyExistError(UserAlreadyExistsException ex){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.error(
                        "User Already Exists",
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(InvalidUserRoleException.class)
    public ResponseEntity<ApiError<Void>> handleInvalidUserRoleError(InvalidUserRoleException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.error(
                        "Invalid User Role",
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiError<Void>> handleEmailNotVerifiedError(EmailNotVerifiedException ex){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.error(
                        "Email not verified",
                        ex.getMessage(),
                        null
                ));
    }

    // Handles Business/Runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError<Void>> handleRuntimeErrors(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.error(
                        "Internal Server Error",
                        ex.getMessage(),
                        null

                ));
    }
}
