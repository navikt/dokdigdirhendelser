package no.nav.dokdigdirhendelser.altinnsubscription.naistexas;

import com.fasterxml.jackson.annotation.JsonProperty;

record NaisTexasToken(@JsonProperty("access_token") String accessToken) {
}
