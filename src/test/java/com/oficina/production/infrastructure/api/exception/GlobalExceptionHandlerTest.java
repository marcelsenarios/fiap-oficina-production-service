package com.oficina.production.infrastructure.api.exception;

import com.oficina.production.domain.exception.BusinessException;
import com.oficina.production.domain.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Handler: GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve tratar BusinessException")
    void deveTratarBusinessException() {
        BusinessException ex = new BusinessException("Regra violada");
        ProblemDetail pd = handler.handleBusinessException(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Regra violada", pd.getDetail());
    }

    @Test
    @DisplayName("Deve tratar EntityNotFoundException")
    void deveTratarEntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Não encontrado");
        ProblemDetail pd = handler.handleEntityNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), pd.getStatus());
        assertEquals("Não encontrado", pd.getDetail());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException")
    void deveTratarMethodArgumentNotValidException() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "campo", "campo obrigatório"));

        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("deveTratarMethodArgumentNotValidException"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ProblemDetail pd = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertNotNull(pd.getProperties().get("errors"));
    }

    @Test
    @DisplayName("Deve tratar exceção genérica")
    void deveTratarGenericException() {
        Exception ex = new RuntimeException("Erro inesperado");
        ProblemDetail pd = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), pd.getStatus());
    }
}
