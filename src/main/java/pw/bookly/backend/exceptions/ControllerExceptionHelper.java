package pw.bookly.backend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import pw.bookly.backend.security.controllers.JwtAuthenticationController;

@ControllerAdvice(annotations = RestController.class)
public class ControllerExceptionHelper {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHelper.class);

    @ExceptionHandler(value = { InvalidFileException.class })
    public ResponseEntity<ExceptionDetails> handleNotFound(InvalidFileException ex) {
        logger.error("Invalid Input Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDetails(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { ResourceNotFoundException.class })
    public ResponseEntity<ExceptionDetails> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource Not Found Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDetails(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { UnauthorizedException.class})
    public ResponseEntity<ExceptionDetails> handleUnauthorized(UnauthorizedException ex) {
        logger.error("Unauthorized Exception: {}", ex.getMessage());
        ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.UNAUTHORIZED, ex.getMessage());
        exceptionDetails.setPath(ex.getPath());
        return new ResponseEntity<>(exceptionDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { AuthenticationException.class})
    public ResponseEntity<ExceptionDetails> handleAuthenticationException(AuthenticationException ex) {
        logger.error("Authentication Exception: {}", ex.getMessage());
        ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.UNAUTHORIZED, ex.getMessage());
        exceptionDetails.setPath(JwtAuthenticationController.AUTHENTICATION_PATH);
        return new ResponseEntity<>(exceptionDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { UserValidationException.class })
    public ResponseEntity<ExceptionDetails> UserValidationException(UserValidationException ex) {
        logger.error("User Validation Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDetails(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { UsernameNotFoundException.class })
    public ResponseEntity<ExceptionDetails> handleBadRequest(UsernameNotFoundException ex) {
        logger.error("Username Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDetails(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
