package no.nav.dokdigdirhendelser.altinn.eventvalidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import no.nav.dokdigdirhendelser.altinn.AltinnEvents;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class EventTimeValidator implements ConstraintValidator<ValiderTime, AltinnEvents> {

	@Override
	public void initialize(ValiderTime constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(AltinnEvents altinnEvents, ConstraintValidatorContext constraintValidatorContext) {
		return Optional.ofNullable(altinnEvents)
				.map(AltinnEvents::time)
				.filter(StringUtils::isNotBlank)
				.flatMap(this::parseTime)
				.isPresent();
	}

	private Optional<OffsetDateTime> parseTime(String time) {
		try {
			return Optional.of(OffsetDateTime.parse(time));
		} catch (DateTimeParseException _) {
			return Optional.empty();
		}
	}
}
