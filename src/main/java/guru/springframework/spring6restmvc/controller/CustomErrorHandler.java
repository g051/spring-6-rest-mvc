package guru.springframework.spring6restmvc.controller;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomErrorHandler {

  @ExceptionHandler
  ResponseEntity<?> handleJPAViolations(TransactionSystemException ex) {

    ResponseEntity.BodyBuilder responseEntity = ResponseEntity.badRequest();

    if (ex.getRootCause() instanceof ConstraintViolationException ve) {
      var errors = ve.getConstraintViolations().stream()
          .map(violation -> {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put(violation.getPropertyPath().toString(), violation.getMessage());
            return errorMap;
          }).toList();

      return responseEntity.body(errors);
    }

    return responseEntity.build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<?> handleBindErrors(MethodArgumentNotValidException ex) {

    var errorList = ex.getFieldErrors().stream()
        .map(fieldError -> {
          Map<String, String> errorMap = new HashMap<>();
          errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
          return errorMap;
        }).collect(Collectors.toList());

    return ResponseEntity.badRequest().body(errorList);
  }
}
