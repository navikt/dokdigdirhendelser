package no.nav.dokdigdirhendelser.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String VALIDERINGSFEIL = "Valideringfeil: {}";

	@ResponseStatus(OK)
	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
		log.error(VALIDERINGSFEIL, ex.getMessage());
		return ResponseEntity.status(OK).body(ex.getMessage());
	}

	@ResponseStatus(OK)
	@ExceptionHandler({IllegalArgumentException.class})
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.error(VALIDERINGSFEIL, ex.getMessage());
		return ResponseEntity.status(OK).body(ex.getMessage());
	}

	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}

}
