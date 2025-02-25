package ru.practicum.ewm.stats.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.stats.server.model.ErrorResponse;
import ru.practicum.ewm.stats.server.exceptions.InvalidDateException;

@RestControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(InvalidDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDateException(InvalidDateException e) {
        return new ErrorResponse("Invalid date format", e.getMessage());
    }
}
