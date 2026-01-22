package no.nav.dokdigdirhendelser.exception;

public class KafkaTechnicalException extends DokDigdirHendelserTechnicalException {
	public KafkaTechnicalException(String message, Throwable cause) {
		super(message, cause);
	}
}
