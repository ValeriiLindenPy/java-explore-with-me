package ru.practicum.ewm.main.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.main.exceptions.*;
import ru.practicum.ewm.main.model.dto.ApiError;
import java.time.LocalDateTime;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalAdviceController {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("Incorrectly made request.")
                .timestamp(LocalDateTime.now())
                .status(BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleDataValidationException (DataValidationException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("Incorrectly made request.")
                .timestamp(LocalDateTime.now())
                .status(BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("The required object was not found.")
                .timestamp(LocalDateTime.now())
                .status(NOT_FOUND)
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(CONFLICT)
    public ApiError handleConflictException(ConflictException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .timestamp(LocalDateTime.now())
                .status(CONFLICT)
                .build();
    }

    @ExceptionHandler(IntegrityException.class)
    @ResponseStatus(CONFLICT)
    public ApiError handleIntegrityException(IntegrityException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("Integrity constraint has been violated.")
                .timestamp(LocalDateTime.now())
                .status(CONFLICT)
                .build();
    }

    @ExceptionHandler(PublishException.class)
    @ResponseStatus(FORBIDDEN)
    public ApiError handlePublishException(PublishException ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .timestamp(LocalDateTime.now())
                .status(FORBIDDEN)
                .build();
    }
}
