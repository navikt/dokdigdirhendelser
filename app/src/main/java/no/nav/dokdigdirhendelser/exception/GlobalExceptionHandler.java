package no.nav.dokdigdirhendelser.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(OK)
	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
		String errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.collect(Collectors.toMap(
						FieldError::getField,
						error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Ugyldig verdi",
						(existing, replacement) -> existing
				)).values().stream().findFirst().orElse("");

		log.error(errors);
		return ProblemDetail.forStatus(OK);
	}

	@ResponseStatus(OK)
	@ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class,
			InvalidFormatException.class})
	public ProblemDetail handleIllegalArgumentException(Exception ex) {
		log.error(ex.getMessage());
		return ProblemDetail.forStatus(OK);
	}

	@ResponseStatus(INTERNAL_SERVER_ERROR)
	@ExceptionHandler({DokDigdirHendelserTechnicalException.class, Exception.class})
	public ProblemDetail handleGenericException(Exception ex) {
		log.error("Feilet teknisk:{}", ex.getMessage(), ex);
		return ProblemDetail.forStatusAndDetail(
				INTERNAL_SERVER_ERROR,
				"Feilet teknisk: " + ex.getMessage()
		);
	}
}
