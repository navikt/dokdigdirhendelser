package no.nav.dokdigdirhendelser.utils;

public final class SafeLog {

	private static final int MAX_LENGTH = 50;

	private SafeLog() {
	}

	public static String sanitize(String value) {
		if (value == null) {
			return "null";
		}

		String sanitized = value.replaceAll("[^a-zA-Z0-9.\\-]", "");

		return sanitized.length() > MAX_LENGTH ? sanitized.substring(0, MAX_LENGTH) : sanitized;
	}

}
