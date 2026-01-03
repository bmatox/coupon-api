package com.coupon.api.controller.advice;

import com.coupon.api.domain.exception.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Erro de validação nos dados enviados");
        problemDetail.setDetail("Um ou mais campos obrigatórios estão inválidos ou ausentes.");

        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .toList();

        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex) {
        // 404 se for "não encontrado", senão 400
        HttpStatus status = ex.getMessage().toLowerCase().contains("não encontrado")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Violação de regra de negócio");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Conflito de dados");
        problemDetail.setDetail("Já existe um cupom cadastrado com este código.");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}