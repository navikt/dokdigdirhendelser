package no.nav.dokdigdirhendelser.config;

import no.nav.dokdigdirhendelser.altinnsubscription.AltinnTokenExchangeConsumer;
import no.nav.dokdigdirhendelser.altinnsubscription.naistexas.Altinn3TokenRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
public class RestClientConfig {

	@Bean
	public RestClient restClient(RestClient.Builder restClientBuilder) {
		return restClientBuilder
				.requestFactory(jdkClientHttpRequestFactory())
				.build();
	}

	@Bean
	public RestClient altinnRestClient(RestClient.Builder restClientBuilder,
									   AltinnTokenExchangeConsumer altinnTokenExchangeConsumer,
									   DokDigdirHendelserProperties dokDigdirHendelserProperties) {
		return restClientBuilder
				.baseUrl(dokDigdirHendelserProperties.altinnUrl())
				.defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
				.requestFactory(jdkClientHttpRequestFactory())
				.requestInterceptor(new Altinn3TokenRequestInterceptor(altinnTokenExchangeConsumer))
				.build();
	}

	private static JdkClientHttpRequestFactory jdkClientHttpRequestFactory() {
		HttpClient httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(10))
				.build();
		JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
		factory.setReadTimeout(Duration.ofSeconds(20));
		return factory;
	}
}
