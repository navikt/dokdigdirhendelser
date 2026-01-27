package no.nav.dokdigdirhendelser.altinn.eventvalidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EventTimeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValiderTime {

	String message() default "Forsendelsesmetadata og ForsendelsesmetadataType må enten begge være satt, eller begge være null.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
