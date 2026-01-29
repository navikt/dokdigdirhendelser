package no.nav.dokdigdirhendelser.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(OK)
	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
		return ProblemDetail.forStatus(OK);
	}

	@ResponseStatus(OK)
	@ExceptionHandler({IllegalArgumentException.class})
	public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
		return ProblemDetail.forStatus(OK);
	}

	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ProblemDetail handleGenericException(Exception ex) {
		return ProblemDetail.forStatusAndDetail(
				INTERNAL_SERVER_ERROR,
				"Feilet teknisk: " + ex.getMessage()
		);
	}
}
