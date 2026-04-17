package no.nav.dokdigdirhendelser.utils;

import java.util.regex.Pattern;

public final class SafeLog {

	private static final Pattern SAFE_PATTERN = Pattern.compile("[^a-zA-Z0-9.\\-]");
	private static final int MAX_LENGTH = 80;

	private SafeLog() {
	}

	public static String sanitize(String value) {
		if (value == null) {
			return "null";
		}

		String sanitized = SAFE_PATTERN.matcher(value).replaceAll("");
		return sanitized.length() > MAX_LENGTH ? sanitized.substring(0, MAX_LENGTH) : sanitized;
	}

}
