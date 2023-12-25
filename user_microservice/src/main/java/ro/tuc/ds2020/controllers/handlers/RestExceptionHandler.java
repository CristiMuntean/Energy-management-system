package ro.tuc.ds2020.controllers.handlers;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.CustomException;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ExceptionHandlerResponseDTO;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        Set<ConstraintViolation<?>> details = e.getConstraintViolations();
        ExceptionHandlerResponseDTO errorInformation = new ExceptionHandlerResponseDTO(e.getMessage(),
                status.getReasonPhrase(),
                status.value(),
                e.getMessage(),
                details,
                request.getDescription(false));
        return handleExceptionInternal(
                e,
                errorInformation,
                new HttpHeaders(),
                status,
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ObjectError> errs = ex.getBindingResult().getAllErrors();
        List<String> details = new ArrayList<>();
        for (ObjectError err : errs) {
            String fieldName = ((FieldError) err).getField();
            String errorMessage = err.getDefaultMessage();
            details.add(fieldName + ":" + errorMessage);
        }
        ExceptionHandlerResponseDTO errorInformation = new ExceptionHandlerResponseDTO(ex.getParameter().getParameterName(),
                status.getReasonPhrase(),
                status.value(),
                MethodArgumentNotValidException.class.getSimpleName(),
                details,
                request.getDescription(false));
        return handleExceptionInternal(
                ex,
                errorInformation,
                new HttpHeaders(),
                status,
                request
        );
    }

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<Object> handleCustomExceptions(CustomException ex,
                                                            WebRequest request) {
        ExceptionHandlerResponseDTO errorInformation = new ExceptionHandlerResponseDTO(ex.getResource(),
                ex.getStatus().getReasonPhrase(),
                ex.getStatus().value(),
                ex.getMessage(),
                ex.getValidationErrors(),
                request.getDescription(false));
        return handleExceptionInternal(
                ex,
                errorInformation,
                new HttpHeaders(),
                ex.getStatus(),
                request
        );
    }


}
