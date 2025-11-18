
package com.bankingsystem.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> build(HttpStatus s,String msg){
        return new ResponseEntity<>(Map.of(
            "timestamp", Instant.now().toString(),
            "status", s.value(),
            "error", s.getReasonPhrase(),
            "message", msg
        ), s);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Object> nf(AccountNotFoundException e){return build(HttpStatus.NOT_FOUND,e.getMessage());}

    @ExceptionHandler({InsufficientBalanceException.class,InvalidAmountException.class})
    public ResponseEntity<Object> bad(RuntimeException e){return build(HttpStatus.BAD_REQUEST,e.getMessage());}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> other(Exception e){return build(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());}
}
