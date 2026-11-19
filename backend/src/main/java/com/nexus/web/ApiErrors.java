package com.nexus.web; import org.springframework.web.bind.annotation.*; import org.springframework.http.*; import org.springframework.security.access.AccessDeniedException; import org.springframework.web.bind.MethodArgumentNotValidException; import java.util.*;
@RestControllerAdvice public class ApiErrors {
 public static class Missing extends RuntimeException{public Missing(String m){super(m);}}
 @ExceptionHandler(Missing.class) ResponseEntity<?> missing(Missing e){return ResponseEntity.status(404).body(Map.of("message",e.getMessage()));}
 @ExceptionHandler(AccessDeniedException.class) ResponseEntity<?> denied(){return ResponseEntity.status(403).body(Map.of("message","You do not have permission for this operation"));}
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class}) ResponseEntity<?> invalid(RuntimeException e){return ResponseEntity.badRequest().body(Map.of("message",Objects.toString(e.getMessage(),"Invalid request")));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(Map.of("message",e.getBindingResult().getFieldErrors().stream().map(f->f.getField()+": "+f.getDefaultMessage()).distinct().toList().toString()));}
 @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class) ResponseEntity<?> conflict(){return ResponseEntity.status(409).body(Map.of("message","Duplicate or conflicting data"));}
 @ExceptionHandler(org.springframework.dao.OptimisticLockingFailureException.class) ResponseEntity<?> retry(){return ResponseEntity.status(409).body(Map.of("message","Incident changed. Refresh and retry."));}
}
