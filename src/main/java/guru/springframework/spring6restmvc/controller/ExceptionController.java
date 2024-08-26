package guru.springframework.spring6restmvc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/* Replaced by NotFoundException @ResponseStatus
   alternatively can use below @ControllerAdvice and @ExceptionHandler to catch and handle exceptions
 */
//@ControllerAdvice
public class ExceptionController {

//  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity handleNotFoundException(Exception ex) {
    System.out.println("Handling Not Found Exception");
//        return new ResponseEntity(HttpStatus.NOT_FOUND);
    return ResponseEntity.notFound().build();
  }
}
