package no.nav.dokdigdirhendelser.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String AVBRYTER_BEHANDLING_MED_FEILMELDING = "Avbryter behandling av hendelse med feilmelding: %s";

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		String feil = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + (error.getDefaultMessage() != null ? error.getDefaultMessage() : "Ugyldig verdi"))
				.collect(Collectors.joining(", "));

		log.error(AVBRYTER_BEHANDLING_MED_FEILMELDING.formatted(feil), ex);

		return ResponseEntity.ok(feil);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public void handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletResponse response) {
		if (ex.getCause() instanceof UnrecognizedPropertyException) {
			log.error(AVBRYTER_BEHANDLING_MED_FEILMELDING.formatted("Hendelse innehold ukjent felt."), ex);
			response.setStatus(BAD_REQUEST.value());
		} else {
			log.error(AVBRYTER_BEHANDLING_MED_FEILMELDING.formatted("Kunne ikke lese JSON-innhold."), ex);
			response.setStatus(OK.value());
		}
	}

	@ResponseStatus(OK)
	@ExceptionHandler(IllegalArgumentException.class)
	public void handleIllegalArgumentException(IllegalArgumentException ex) {
		log.error(ex.getMessage());
	}

	@ResponseStatus(OK)
	@ExceptionHandler(HendelseTypeBehandlesIkkeException.class)
	public void handleEventTypeBehandlesIkkeException(HendelseTypeBehandlesIkkeException ex) {
		log.info(ex.getMessage());
	}

	@ExceptionHandler({DokDigdirHendelserTechnicalException.class, Exception.class})
	public ProblemDetail handleGenericException(Exception ex) {
		log.error("Teknisk feil med feilmelding:{}", ex.getMessage(), ex);
		return ProblemDetail.forStatusAndDetail(
				INTERNAL_SERVER_ERROR,
				"Teknisk feil med feilmelding: " + ex.getMessage()
		);
	}
}
